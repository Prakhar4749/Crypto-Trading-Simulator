import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { useEffect, useState, useMemo } from "react";
import { Avatar, AvatarImage } from "@/components/ui/avatar";
import TradingHistory from "./TradingHistory";
import { useNavigate } from "react-router-dom";
import { useAssets } from "@/contexts/AssetsContext";
import { useAuth } from "@/contexts/AuthContext";
import { 
  History, 
  PieChart, 
  TrendingUp, 
  TrendingDown, 
  ArrowUpRight, 
  Wallet,
  Activity
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { formatUSD } from "@/Util/currencyUtils";
import { Badge } from "@/components/ui/badge";

const Portfolio = () => {
  const navigate = useNavigate();
  const [currentTab, setCurrentTab] = useState("portfolio");
  const { userAssets, getUserAssets } = useAssets();

  useEffect(() => {
    getUserAssets();
  }, []);

  const stats = useMemo(() => {
    if (!userAssets || userAssets.length === 0) return null;

    let totalInvested = 0;
    let currentValue = 0;
    let dayProfitLoss = 0;

    userAssets.forEach(item => {
      const price = item.coin?.current_price || 0;
      const buyPrice = item.buyPrice || 0;
      const dayChange = item.coin?.price_change_24h || 0;

      totalInvested += (buyPrice * item.quantity);
      currentValue += (price * item.quantity);
      dayProfitLoss += (dayChange * item.quantity);
    });

    const totalPL = currentValue - totalInvested;
    const totalPLPercentage = totalInvested !== 0 ? (totalPL / totalInvested) * 100 : 0;
    const dayPLPercentage = (currentValue - dayProfitLoss) !== 0 
      ? (dayProfitLoss / (currentValue - dayProfitLoss)) * 100 
      : 0;

    return {
      totalInvested,
      currentValue,
      totalPL,
      totalPLPercentage,
      dayProfitLoss,
      dayPLPercentage
    };
  }, [userAssets]);

  return (
    <div className="bg-app-bg min-h-screen p-6">
      <div className="max-w-6xl mx-auto space-y-8">
        {/* Page Header */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <h1 className="text-app-textPrimary dark:text-white font-black text-3xl tracking-tight">Portfolio</h1>
            <p className="text-app-textSecondary dark:text-gray-500 text-sm font-medium">Manage and analyze your crypto holdings</p>
          </div>
          
          <div className="flex bg-white dark:bg-[#1a1a2e] rounded-xl p-1 border border-app-border dark:border-gray-800 shadow-sm">
            <Button
              variant={currentTab === "portfolio" ? "secondary" : "ghost"}
              size="sm"
              onClick={() => setCurrentTab("portfolio")}
              className={`rounded-lg px-6 h-9 font-bold text-xs transition-all ${
                currentTab === "portfolio" 
                  ? "bg-brand-primary text-white shadow-md hover:bg-brand-dark" 
                  : "text-app-textSecondary dark:text-gray-400 hover:bg-app-bg dark:hover:bg-gray-800"
              }`}
            >
              <PieChart className="w-3.5 h-3.5 mr-2" /> PORTFOLIO
            </Button>
            <Button
              variant={currentTab === "history" ? "secondary" : "ghost"}
              size="sm"
              onClick={() => setCurrentTab("history")}
              className={`rounded-lg px-6 h-9 font-bold text-xs transition-all ${
                currentTab === "history" 
                  ? "bg-brand-primary text-white shadow-md hover:bg-brand-dark" 
                  : "text-app-textSecondary dark:text-gray-400 hover:bg-app-bg dark:hover:bg-gray-800"
              }`}
            >
              <History className="w-3.5 h-3.5 mr-2" /> HISTORY
            </Button>
          </div>
        </div>

        {currentTab === "portfolio" ? (
          <>
            {/* Analysis Grid */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {/* Total Value Card */}
              <div className="bg-white dark:bg-[#1a1a2e] rounded-card p-6 border border-app-border dark:border-gray-800 shadow-sm relative overflow-hidden group">
                <div className="absolute -right-4 -bottom-4 opacity-5 group-hover:scale-110 transition-transform duration-500">
                  <Wallet size={120} className="text-brand-primary" />
                </div>
                <p className="text-app-textSecondary dark:text-gray-500 text-xs font-black uppercase tracking-widest mb-1">Total Value</p>
                <h2 className="text-3xl font-black text-app-textPrimary dark:text-white mb-2">
                  {formatUSD(stats?.currentValue || 0)}
                </h2>
                <div className="flex items-center gap-2">
                  <Badge className="bg-app-bg dark:bg-gray-900 text-app-textSecondary dark:text-gray-400 border-none px-2 py-0.5 text-[10px] font-bold">
                    Invested: {formatUSD(stats?.totalInvested || 0)}
                  </Badge>
                </div>
              </div>

              {/* Total Returns Card */}
              <div className="bg-white dark:bg-[#1a1a2e] rounded-card p-6 border border-app-border dark:border-gray-800 shadow-sm relative overflow-hidden group">
                <div className="absolute -right-4 -bottom-4 opacity-5 group-hover:scale-110 transition-transform duration-500">
                  <TrendingUp size={120} className="text-brand-primary" />
                </div>
                <p className="text-app-textSecondary dark:text-gray-500 text-xs font-black uppercase tracking-widest mb-1">Total Returns</p>
                <h2 className={`text-3xl font-black mb-2 ${
                  (stats?.totalPL || 0) >= 0 ? "text-brand-primary" : "text-app-error"
                }`}>
                  {(stats?.totalPL || 0) >= 0 ? "+" : ""}{formatUSD(stats?.totalPL || 0)}
                </h2>
                <div className="flex items-center gap-2">
                  <span className={`text-xs font-black flex items-center ${
                    (stats?.totalPL || 0) >= 0 ? "text-brand-primary" : "text-app-error"
                  }`}>
                    {(stats?.totalPL || 0) >= 0 ? <TrendingUp className="w-3 h-3 mr-1" /> : <TrendingDown className="w-3 h-3 mr-1" />}
                    {stats?.totalPLPercentage.toFixed(2)}%
                  </span>
                  <span className="text-[10px] text-app-textSecondary dark:text-gray-500 font-bold uppercase">All Time</span>
                </div>
              </div>

              {/* 1-Day Returns Card */}
              <div className="bg-white dark:bg-[#1a1a2e] rounded-card p-6 border border-app-border dark:border-gray-800 shadow-sm relative overflow-hidden group">
                <div className="absolute -right-4 -bottom-4 opacity-5 group-hover:scale-110 transition-transform duration-500">
                  <Activity size={120} className="text-brand-primary" />
                </div>
                <p className="text-app-textSecondary dark:text-gray-500 text-xs font-black uppercase tracking-widest mb-1">1-Day Returns</p>
                <h2 className={`text-3xl font-black mb-2 ${
                  (stats?.dayProfitLoss || 0) >= 0 ? "text-brand-primary" : "text-app-error"
                }`}>
                  {(stats?.dayProfitLoss || 0) >= 0 ? "+" : ""}{formatUSD(stats?.dayProfitLoss || 0)}
                </h2>
                <div className="flex items-center gap-2">
                  <span className={`text-xs font-black flex items-center ${
                    (stats?.dayProfitLoss || 0) >= 0 ? "text-brand-primary" : "text-app-error"
                  }`}>
                    {(stats?.dayProfitLoss || 0) >= 0 ? <ArrowUpRight className="w-3 h-3 mr-1" /> : <TrendingDown className="w-3 h-3 mr-1" />}
                    {Math.abs(stats?.dayPLPercentage || 0).toFixed(2)}%
                  </span>
                  <span className="text-[10px] text-app-textSecondary dark:text-gray-500 font-bold uppercase">Last 24H</span>
                </div>
              </div>
            </div>

            {/* Holdings Table */}
            <div className="space-y-4">
              <div className="flex items-center gap-2 px-1">
                <div className="w-1 h-4 bg-brand-primary rounded-full"></div>
                <h3 className="text-sm font-black text-app-textPrimary dark:text-white uppercase tracking-widest">Asset Details</h3>
              </div>
              
              <div className="bg-white dark:bg-[#1a1a2e] rounded-card shadow-sm border border-app-border dark:border-gray-800 overflow-hidden">
                <div className="overflow-x-auto">
                  <Table>
                  <TableHeader className="bg-app-bg dark:bg-gray-900/50">
                    <TableRow className="hover:bg-transparent border-app-border dark:border-gray-800">
                      <TableHead className="py-4 px-6 text-app-textSecondary dark:text-gray-400 text-[10px] font-black uppercase tracking-widest">Asset</TableHead>
                      <TableHead className="text-app-textSecondary dark:text-gray-400 text-[10px] font-black uppercase tracking-widest">Price</TableHead>
                      <TableHead className="text-app-textSecondary dark:text-gray-400 text-[10px] font-black uppercase tracking-widest">Holdings</TableHead>
                      <TableHead className="text-app-textSecondary dark:text-gray-400 text-[10px] font-black uppercase tracking-widest">Returns</TableHead>
                      <TableHead className="text-right px-6 text-app-textSecondary dark:text-gray-400 text-[10px] font-black uppercase tracking-widest">Current Value</TableHead>
                    </TableRow>
                  </TableHeader>

                  <TableBody>
                    {userAssets?.length > 0 ? (
                      userAssets.map((item) => {
                        const price = item.coin?.current_price || 0;
                        const buyPrice = item.buyPrice || 0;
                        const profit = (price - buyPrice) * item.quantity;
                        const profitPercentage = buyPrice !== 0 ? ((price - buyPrice) / buyPrice) * 100 : 0;

                        return (
                          <TableRow
                            key={item.id}
                            className="cursor-pointer border-app-border dark:border-gray-800 hover:bg-brand-light/30 dark:hover:bg-gray-900/30 transition-colors"
                            onClick={() => navigate(`/market/${item.coin?.id || item.coinId}`)}
                          >
                            <TableCell className="px-6 py-5">
                              <div className="flex items-center gap-3">
                                <Avatar className="h-9 w-9 ring-1 ring-app-border dark:ring-gray-700 p-0.5 bg-white dark:bg-gray-900">
                                  <AvatarImage src={item.coin?.image} alt={item.coin?.symbol} />
                                </Avatar>
                                <div className="flex flex-col">
                                  <span className="text-app-textPrimary dark:text-white font-bold text-sm">{item.coin?.name || item.coinId}</span>
                                  <span className="text-app-textSecondary dark:text-gray-500 text-[10px] font-black uppercase tracking-wider">{item.coin?.symbol || 'N/A'}</span>
                                </div>
                              </div>
                            </TableCell>
                            <TableCell>
                              <div className="flex flex-col">
                                <span className="text-app-textPrimary dark:text-gray-300 font-bold text-sm">{formatUSD(price)}</span>
                                <span className="text-[10px] text-app-textSecondary dark:text-gray-500 font-medium tracking-tight">Avg: {formatUSD(buyPrice)}</span>
                              </div>
                            </TableCell>
                            <TableCell>
                              <div className="flex flex-col">
                                <span className="text-app-textPrimary dark:text-gray-300 font-bold text-sm">{item.quantity.toFixed(4)}</span>
                                <span className="text-app-textSecondary dark:text-gray-500 text-[10px] font-black uppercase">{item.coin?.symbol || 'TOKENS'}</span>
                              </div>
                            </TableCell>
                            <TableCell>
                              <div className="flex flex-col">
                                <span className={`text-sm font-bold ${profit >= 0 ? "text-brand-primary" : "text-app-error"}`}>
                                  {profit >= 0 ? "+" : ""}{formatUSD(profit)}
                                </span>
                                <span className={`text-[10px] font-black ${profit >= 0 ? "text-brand-primary" : "text-app-error"}`}>
                                  {profit >= 0 ? "+" : ""}{profitPercentage.toFixed(2)}%
                                </span>
                              </div>
                            </TableCell>
                            <TableCell className="text-right px-6">
                              <div className="flex flex-col items-end">
                                <span className="text-app-textPrimary dark:text-white font-bold text-sm">
                                  {formatUSD(price * item.quantity)}
                                </span>
                                <span className={`text-[10px] font-black px-1.5 py-0.5 rounded-sm ${
                                  (item.coin?.price_change_percentage_24h || 0) >= 0 
                                    ? "bg-brand-light dark:bg-brand-dark/20 text-brand-primary" 
                                    : "bg-red-50 dark:bg-red-900/20 text-app-error"
                                }`}>
                                  24H: {item.coin?.price_change_percentage_24h?.toFixed(2)}%
                                </span>
                              </div>
                            </TableCell>
                          </TableRow>
                        );
                      })
                    ) : (
                      <TableRow>
                        <TableCell colSpan={5} className="py-24 text-center">
                          <div className="flex flex-col items-center justify-center space-y-4">
                            <div className="bg-app-bg dark:bg-gray-900/50 p-6 rounded-full">
                              <PieChart className="w-12 h-12 text-app-textSecondary dark:text-gray-700 opacity-20" />
                            </div>
                            <div className="space-y-1">
                              <p className="text-app-textPrimary dark:text-white font-bold text-lg">Your portfolio is empty</p>
                              <p className="text-app-textSecondary dark:text-gray-500 text-sm max-w-xs mx-auto mb-4">
                                You haven't made any trades yet. Start exploring the market to build your portfolio.
                              </p>
                            </div>
                            <Button 
                              onClick={() => navigate("/")}
                              className="bg-brand-primary hover:bg-brand-dark text-white font-bold px-8 h-12 rounded-xl shadow-lg transition-all active:scale-[0.98]"
                            >
                              START TRADING
                            </Button>
                          </div>
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
                </div>
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
