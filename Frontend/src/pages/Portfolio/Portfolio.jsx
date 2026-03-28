import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { useEffect, useState } from "react";
import { Avatar, AvatarImage } from "@/components/ui/avatar";
import TradingHistory from "./TradingHistory";
import { useNavigate } from "react-router-dom";
import { useAssets } from "@/contexts/AssetsContext";
import { useAuth } from "@/contexts/AuthContext";
import { History, PieChart } from "lucide-react";
import { Button } from "@/components/ui/button";

const Portfolio = () => {
  const navigate = useNavigate();
  const [currentTab, setCurrentTab] = useState("portfolio");
  const { userAssets, loading, getUserAssets } = useAssets();
  const { jwt } = useAuth();

  useEffect(() => {
    console.log("[Portfolio] mounted, fetching assets");
    getUserAssets(jwt || localStorage.getItem("jwt"));
  }, []);

  const totalPortfolioValue = userAssets?.reduce((acc, item) => 
    acc + (item.coin.current_price * item.quantity), 0
  ) || 0;

  return (
    <div className="bg-app-bg min-h-screen p-6">
      <div className="max-w-6xl mx-auto space-y-6">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-app-textPrimary font-bold text-2xl">My Portfolio</h1>
          <div className="flex bg-white rounded-input p-1 border border-app-border shadow-sm">
            <Button
              variant={currentTab === "portfolio" ? "secondary" : "ghost"}
              size="sm"
              onClick={() => setCurrentTab("portfolio")}
              className={`rounded-md px-4 h-9 ${currentTab === "portfolio" ? "bg-brand-light text-brand-primary hover:bg-brand-light/80" : "text-app-textSecondary"}`}
            >
              <PieChart className="w-4 h-4 mr-2" /> Portfolio
            </Button>
            <Button
              variant={currentTab === "history" ? "secondary" : "ghost"}
              size="sm"
              onClick={() => setCurrentTab("history")}
              className={`rounded-md px-4 h-9 ${currentTab === "history" ? "bg-brand-light text-brand-primary hover:bg-brand-light/80" : "text-app-textSecondary"}`}
            >
              <History className="w-4 h-4 mr-2" /> History
            </Button>
          </div>
        </div>

        {currentTab === "portfolio" ? (
          <>
            {/* Portfolio Value Card */}
            <div className="bg-brand-primary rounded-card p-6 text-white shadow-cardHover relative overflow-hidden">
              <div className="absolute top-0 right-0 p-8 opacity-10">
                <PieChart size={100} />
              </div>
              
              <div className="relative z-10 space-y-2">
                <p className="text-white/70 text-sm font-medium">Total Portfolio Value</p>
                <div className="flex items-baseline gap-2">
                  <p className="text-white font-bold text-4xl mt-1">
                    ${totalPortfolioValue.toLocaleString()}
                  </p>
                  <span className="text-white/80 text-xs font-semibold uppercase tracking-wider">USD</span>
                </div>
              </div>
            </div>

            {/* Holdings Table */}
            <div className="bg-white rounded-card shadow-card border border-app-border overflow-hidden">
              <div className="overflow-x-auto">
                <Table>
                <TableHeader className="bg-app-bg">
                  <TableRow className="hover:bg-transparent border-app-border">
                    <TableHead className="py-4 px-6 text-app-textSecondary text-xs font-semibold uppercase tracking-wide">Asset</TableHead>
                    <TableHead className="text-app-textSecondary text-xs font-semibold uppercase tracking-wide">Price</TableHead>
                    <TableHead className="text-app-textSecondary text-xs font-semibold uppercase tracking-wide">Holdings</TableHead>
                    <TableHead className="text-app-textSecondary text-xs font-semibold uppercase tracking-wide">24H Change</TableHead>
                    <TableHead className="text-right px-6 text-app-textSecondary text-xs font-semibold uppercase tracking-wide">Current Value</TableHead>
                  </TableRow>
                </TableHeader>

                <TableBody>
                  {userAssets?.length > 0 ? (
                    userAssets.map((item) => (
                      <TableRow
                        key={item.id}
                        className="cursor-pointer border-app-border hover:bg-brand-light transition-colors"
                        onClick={() => navigate(`/market/${item.coin.id}`)}
                      >
                        <TableCell className="px-6 py-4">
                          <div className="flex items-center gap-3">
                            <Avatar className="h-8 w-8 ring-1 ring-app-border">
                              <AvatarImage src={item.coin.image} alt={item.coin.symbol} />
                            </Avatar>
                            <div className="flex flex-col">
                              <span className="text-app-textPrimary font-semibold text-sm">{item.coin.name}</span>
                              <span className="text-app-textSecondary text-[10px] font-bold uppercase tracking-wider">{item.coin.symbol}</span>
                            </div>
                          </div>
                        </TableCell>
                        <TableCell className="text-app-textPrimary font-medium text-sm">
                          ${item.coin.current_price.toLocaleString()}
                        </TableCell>
                        <TableCell>
                          <div className="flex flex-col">
                            <span className="text-app-textPrimary font-semibold text-sm">{item.quantity}</span>
                            <span className="text-app-textSecondary text-xs">Tokens</span>
                          </div>
                        </TableCell>
                        <TableCell>
                          <span
                            className={`px-2 py-1 rounded-pill text-[10px] font-bold ${
                              item.coin.price_change_percentage_24h < 0
                                ? "bg-red-50 text-app-error"
                                : "bg-brand-light text-brand-primary"
                            }`}
                          >
                            {item.coin.price_change_percentage_24h?.toFixed(2)}%
                          </span>
                        </TableCell>
                        <TableCell className="text-right px-6">
                          <div className="flex flex-col items-end">
                            <span className="text-app-textPrimary font-medium">
                              ${(item.coin.current_price * item.quantity).toLocaleString()}
                            </span>
                            <span className={`text-[10px] font-bold ${item.coin.price_change_24h >= 0 ? "text-brand-primary" : "text-app-error"}`}>
                              {item.coin.price_change_24h >= 0 ? "+" : ""}{(item.coin.price_change_24h * item.quantity).toLocaleString()} USD
                            </span>
                          </div>
                        </TableCell>
                      </TableRow>
                    ))
                  ) : (
                    <TableRow>
                      <TableCell colSpan={5} className="py-20 text-center">
                        <div className="flex flex-col items-center justify-center space-y-3">
                          <PieChart className="w-10 h-10 text-app-textSecondary opacity-30" />
                          <p className="text-app-textSecondary text-sm font-medium">No assets in portfolio yet</p>
                          <Button 
                            onClick={() => navigate("/")}
                            className="bg-brand-primary hover:bg-brand-dark text-white rounded-input"
                          >
                            Start Trading
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
              </div>
            </div>
          </>
        ) : (
          <TradingHistory />
        )}
      </div>
    </div>
  );
};

export default Portfolio;
