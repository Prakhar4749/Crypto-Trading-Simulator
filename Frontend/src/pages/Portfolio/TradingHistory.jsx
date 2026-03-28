import { useEffect, useState } from "react";
import { Avatar, AvatarImage } from "@/components/ui/avatar";
import { calculateProfit } from "@/Util/calculateProfit";
import { readableDate } from "@/Util/readableDate";
import { useOrder } from "@/contexts/OrderContext";
import { useAuth } from "@/contexts/AuthContext";
import { Button } from "@/components/ui/button";
import { History, TrendingUp, TrendingDown } from "lucide-react";

const TradingHistory = () => {
  const [filter, setFilter] = useState("ALL");
  const { orders, getAllOrdersOfUser } = useOrder();
  const { jwt } = useAuth();

  useEffect(() => {
    getAllOrdersOfUser();
  }, []);

  const filteredOrders = orders?.filter(order => 
    filter === "ALL" ? true : order.orderType === filter
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-app-textPrimary font-bold text-lg">Order History</h2>
        <div className="flex bg-white rounded-input p-1 border border-app-border shadow-sm">
          {["ALL", "BUY", "SELL"].map((type) => (
            <Button
              key={type}
              variant={filter === type ? "secondary" : "ghost"}
              size="sm"
              onClick={() => setFilter(type)}
              className={`rounded-md px-4 h-8 text-[11px] font-bold uppercase tracking-wider ${
                filter === type ? "bg-brand-light text-brand-primary hover:bg-brand-light/80" : "text-app-textSecondary"
              }`}
            >
              {type}
            </Button>
          ))}
        </div>
      </div>

      <div className="space-y-3">
        {filteredOrders?.length > 0 ? (
          filteredOrders.map((item) => (
            <div 
              key={item.id}
              className="bg-white rounded-card shadow-card border border-app-border p-4 flex items-center justify-between hover:border-brand-primary/30 transition-all"
            >
              <div className="flex items-center gap-4">
                <div className={`p-2.5 rounded-full ${item.orderType === "BUY" ? "bg-brand-light" : "bg-red-50"}`}>
                  {item.orderType === "BUY" ? (
                    <TrendingUp className="w-5 h-5 text-brand-primary" />
                  ) : (
                    <TrendingDown className="w-5 h-5 text-app-error" />
                  )}
                </div>
                <div className="flex items-center gap-3">
                  <Avatar className="h-10 w-10 ring-1 ring-app-border">
                    <AvatarImage src={item.coin?.image} alt={item.coin?.symbol} />
                  </Avatar>
                  <div className="space-y-0.5">
                    <div className="flex items-center gap-2">
                      <span className="text-app-textPrimary font-semibold">{item.coin?.name || item.coinId}</span>
                      <span className={`px-2 py-0.5 rounded-pill text-[10px] font-bold uppercase ${
                        item.orderType === "BUY" ? "bg-brand-light text-brand-primary" : "bg-red-50 text-app-error"
                      }`}>
                        {item.orderType}
                      </span>
                    </div>
                    <p className="text-app-textSecondary text-xs">
                      {readableDate(item.timestamp).date} • {readableDate(item.timestamp).time}
                    </p>
                  </div>
                </div>
              </div>

              <div className="flex items-center gap-8">
                <div className="text-right hidden sm:block">
                  <p className="text-app-textSecondary text-[10px] font-bold uppercase tracking-wider">Asset Price</p>
                  <p className="text-app-textPrimary font-semibold text-sm">
                    ${(item.orderType === 'BUY' ? item.buyPrice : item.sellPrice)?.toLocaleString()}
                  </p>
                </div>
                <div className="text-right">
                  <p className="text-app-textSecondary text-[10px] font-bold uppercase tracking-wider">Total Amount</p>
                  <p className="text-app-textPrimary font-bold text-sm">
                    ${item.price?.toLocaleString()}
                  </p>
                  {item.orderType === "SELL" && (
                    <p className={`text-[10px] font-bold ${calculateProfit(item) >= 0 ? "text-brand-primary" : "text-app-error"}`}>
                      {calculateProfit(item) >= 0 ? "+" : ""}{calculateProfit(item).toLocaleString()} USD
                    </p>
                  )}
                </div>
              </div>
            </div>
          ))
        ) : (
          <div className="bg-white rounded-card shadow-card border border-app-border py-20 flex flex-col items-center justify-center text-center space-y-3">
            <History className="w-10 h-10 text-app-textSecondary opacity-30" />
            <p className="text-app-textSecondary text-sm font-medium">No trading history found</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default TradingHistory;
