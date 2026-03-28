import { useWallet } from "@/contexts/WalletContext";
import { useOrder } from "@/contexts/OrderContext";
import { useEffect } from "react";
import { useAuth } from "@/contexts/AuthContext";
import { TrendingUp, TrendingDown, ArrowUpRight, ArrowDownLeft, Clock, History } from "lucide-react";
import { readableDate } from "@/Util/readableDate";

import { formatUSD } from "@/Util/currencyUtils";
import { Badge } from "@/components/ui/badge";

const Activity = () => {
  const { transactions, getWalletTransactions } = useWallet();
  const { orders, getAllOrdersOfUser } = useOrder();
  const { jwt } = useAuth();

  useEffect(() => {
    const currentJwt = jwt || localStorage.getItem("jwt");
    getWalletTransactions(currentJwt);
    getAllOrdersOfUser(currentJwt);
  }, []);

  // Combine and sort activities by timestamp/date
  const combinedActivities = [
    ...(transactions?.map(t => ({ ...t, activityType: 'WALLET', timestamp: new Date(t.date).getTime() })) || []),
    ...(orders?.map(o => ({ ...o, activityType: 'ORDER', timestamp: o.timestamp })) || [])
  ].sort((a, b) => b.timestamp - a.timestamp);

  return (
    <div className="bg-app-bg min-h-screen p-6">
      <div className="max-w-4xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-3">
            <div className="bg-brand-light p-2 rounded-lg">
              <Clock className="h-6 w-6 text-brand-primary" />
            </div>
            <h1 className="text-app-textPrimary font-bold text-2xl">Recent Activity</h1>
          </div>
          <Badge className="bg-brand-light text-brand-primary border-none text-[10px] font-bold uppercase tracking-wider px-3 py-1">
            Simulator
          </Badge>
        </div>

        <div className="bg-white rounded-card shadow-card border border-app-border overflow-hidden">
          {combinedActivities.length > 0 ? (
            <div className="divide-y divide-app-border">
              {combinedActivities.map((item, index) => {
                const isWallet = item.activityType === 'WALLET';
                const isPositive = isWallet ? item.amount > 0 : item.orderType === 'SELL';
                
                return (
                  <div 
                    key={index} 
                    className="flex items-center justify-between px-6 py-4 hover:bg-app-bg transition-colors"
                  >
                    <div className="flex items-center gap-4">
                      <div className={`p-2.5 rounded-full ${isPositive ? 'bg-brand-light' : 'bg-red-50'}`}>
                        {isWallet ? (
                          isPositive ? <ArrowDownLeft className="w-5 h-5 text-brand-primary" /> : <ArrowUpRight className="h-5 w-5 text-app-error" />
                        ) : (
                          isPositive ? <TrendingUp className="w-5 h-5 text-brand-primary" /> : <TrendingDown className="w-5 h-5 text-app-error" />
                        )}
                      </div>
                      <div className="space-y-0.5">
                        <p className="text-app-textPrimary font-medium text-sm">
                          {isWallet ? (item.purpose || "Wallet Transaction") : `${item.orderType} ${item.orderItem.coin.name}`}
                        </p>
                        <p className="text-app-textSecondary text-xs">
                          {isWallet ? item.date : `${readableDate(item.timestamp).date} ${readableDate(item.timestamp).time}`}
                        </p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className={`font-semibold ${isPositive ? "text-brand-primary" : "text-app-error"}`}>
                        {isPositive ? "+" : "-"}{formatUSD(Math.abs(isWallet ? item.amount : item.price))}
                      </p>
                      <p className="text-[10px] text-app-textSecondary uppercase font-bold tracking-tighter">
                        {isWallet ? "Wallet" : "Trading"}
                      </p>
                    </div>
                  </div>
                );
              })}
            </div>
          ) : (
            <div className="py-24 flex flex-col items-center justify-center text-center space-y-3">
              <History className="w-12 h-12 text-app-textSecondary opacity-30" />
              <p className="text-app-textSecondary text-sm font-medium">No recent activities found</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Activity;
