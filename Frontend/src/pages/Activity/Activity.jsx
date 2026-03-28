import { useWallet } from "@/contexts/WalletContext";
import { useOrder } from "@/contexts/OrderContext";
import { useEffect, useState, useMemo } from "react";
import { useAuth } from "@/contexts/AuthContext";
import { 
  TrendingUp, 
  TrendingDown, 
  ArrowUpRight, 
  ArrowDownLeft, 
  Clock, 
  History,
  Filter,
  ArrowUpDown
} from "lucide-react";

import { formatUSD } from "@/Util/currencyUtils";
import { Badge } from "@/components/ui/badge";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const Activity = () => {
  const { transactions, getWalletTransactions } = useWallet();
  const { orders, getAllOrdersOfUser } = useOrder();
  
  const [filterType, setFilterType] = useState("ALL"); // ALL, WALLET, BUY, SELL
  const [sortBy, setBySort] = useState("LATEST"); // LATEST, OLDEST, AMOUNT_HIGH, AMOUNT_LOW

  useEffect(() => {
    getWalletTransactions();
    getAllOrdersOfUser();
  }, []);

  // Helper for IST Formatting
  const formatIST = (timestamp) => {
    try {
      return new Intl.DateTimeFormat('en-IN', {
        timeZone: 'Asia/Kolkata',
        year: 'numeric',
        month: 'short',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
      }).format(new Date(timestamp));
    } catch (e) {
      return new Date(timestamp).toLocaleString();
    }
  };

  const processedActivities = useMemo(() => {
    // 1. Combine and Normalize Timestamps
    let combined = [
      ...(transactions?.map(t => ({ 
        ...t, 
        activityType: 'WALLET', 
        // Ensure numeric timestamp for accurate sorting
        timestamp: t.date ? new Date(t.date).getTime() : 0,
        displayAmount: Math.abs(t.amount)
      })) || []),
      ...(orders?.map(o => ({ 
        ...o, 
        activityType: 'ORDER', 
        // Ensure numeric timestamp for accurate sorting
        timestamp: o.timestamp ? new Date(o.timestamp).getTime() : 0,
        displayAmount: Number(o.price)
      })) || [])
    ];

    // 2. Filter logic
    if (filterType !== "ALL") {
      if (filterType === "WALLET") {
        combined = combined.filter(item => item.activityType === "WALLET");
      } else if (filterType === "TRADES") {
        combined = combined.filter(item => item.activityType === "ORDER");
      } else if (filterType === "BUY" || filterType === "SELL") {
        combined = combined.filter(item => 
          item.activityType === "ORDER" && item.orderType === filterType
        );
      }
    }

    // 3. Sort logic (numeric comparison)
    return combined.sort((a, b) => {
      switch (sortBy) {
        case "OLDEST":
          return a.timestamp - b.timestamp;
        case "AMOUNT_HIGH":
          return b.displayAmount - a.displayAmount;
        case "AMOUNT_LOW":
          return a.displayAmount - b.displayAmount;
        case "LATEST":
        default:
          return b.timestamp - a.timestamp;
      }
    });
  }, [transactions, orders, filterType, sortBy]);

  return (
    <div className="bg-app-bg min-h-screen p-6">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
          <div className="flex items-center gap-3">
            <div className="bg-brand-light dark:bg-brand-dark/20 p-2 rounded-lg">
              <Clock className="h-6 w-6 text-brand-primary" />
            </div>
            <div>
              <h1 className="text-app-textPrimary dark:text-white font-bold text-2xl">Recent Activity</h1>
              <p className="text-app-textSecondary text-xs">Track your transactions and trades</p>
            </div>
          </div>
          
          <div className="flex flex-wrap items-center gap-3">
            {/* Filter Dropdown */}
            <div className="flex items-center gap-2 bg-white dark:bg-[#1a1a2e] border border-app-border dark:border-gray-800 rounded-md px-3 py-1 shadow-sm">
              <Filter className="h-3.5 w-3.5 text-app-textSecondary" />
              <Select value={filterType} onValueChange={setFilterType}>
                <SelectTrigger className="border-none bg-transparent h-8 w-[110px] focus:ring-0 text-xs font-bold p-0">
                  <SelectValue placeholder="Filter" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ALL">All Activity</SelectItem>
                  <SelectItem value="WALLET">Wallet Only</SelectItem>
                  <SelectItem value="TRADES">Trades Only</SelectItem>
                  <SelectItem value="BUY">↳ Buy Only</SelectItem>
                  <SelectItem value="SELL">↳ Sell Only</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* Sort Dropdown */}
            <div className="flex items-center gap-2 bg-white dark:bg-[#1a1a2e] border border-app-border dark:border-gray-800 rounded-md px-3 py-1 shadow-sm">
              <ArrowUpDown className="h-3.5 w-3.5 text-app-textSecondary" />
              <Select value={sortBy} onValueChange={setBySort}>
                <SelectTrigger className="border-none bg-transparent h-8 w-[130px] focus:ring-0 text-xs font-bold p-0">
                  <SelectValue placeholder="Sort" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="LATEST">Latest First</SelectItem>
                  <SelectItem value="OLDEST">Oldest First</SelectItem>
                  <SelectItem value="AMOUNT_HIGH">Highest Amount</SelectItem>
                  <SelectItem value="AMOUNT_LOW">Lowest Amount</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </div>

        {/* Content */}
        <div className="bg-white dark:bg-[#1a1a2e] rounded-card shadow-card border border-app-border dark:border-gray-800 overflow-hidden transition-all">
          {processedActivities.length > 0 ? (
            <div className="divide-y divide-app-border dark:divide-gray-800">
              {processedActivities.map((item, index) => {
                const isWallet = item.activityType === 'WALLET';
                const isPositive = isWallet ? item.amount > 0 : item.orderType === 'SELL';
                
                return (
                  <div 
                    key={`${item.activityType}-${item.id || index}`} 
                    className="flex items-center justify-between px-6 py-5 hover:bg-app-bg dark:hover:bg-gray-900/30 transition-colors"
                  >
                    <div className="flex items-center gap-4">
                      <div className={`p-2.5 rounded-full ${
                        isPositive 
                          ? 'bg-brand-light dark:bg-brand-dark/20' 
                          : 'bg-red-50 dark:bg-red-900/10'
                      }`}>
                        {isWallet ? (
                          isPositive 
                            ? <ArrowDownLeft className="w-5 h-5 text-brand-primary" /> 
                            : <ArrowUpRight className="h-5 w-5 text-app-error" />
                        ) : (
                          isPositive 
                            ? <TrendingUp className="w-5 h-5 text-brand-primary" /> 
                            : <TrendingDown className="w-5 h-5 text-app-error" />
                        )}
                      </div>
                      <div className="space-y-0.5">
                        <div className="flex items-center gap-2">
                          <p className="text-app-textPrimary dark:text-white font-semibold text-sm">
                            {isWallet ? (item.purpose || "Wallet Transaction") : `${item.orderType} ${item.coin?.name || item.coinId}`}
                          </p>
                          <Badge className={`text-[9px] h-4 px-1.5 font-black uppercase tracking-tighter ${
                            isWallet 
                              ? "bg-blue-50 text-blue-600 dark:bg-blue-900/20" 
                              : item.orderType === 'BUY' 
                                ? "bg-green-50 text-green-600 dark:bg-green-900/20"
                                : "bg-red-50 text-red-600 dark:bg-red-900/20"
                          }`}>
                            {isWallet ? "Wallet" : item.orderType}
                          </Badge>
                        </div>
                        <p className="text-app-textSecondary dark:text-gray-500 text-[11px] font-medium">
                          {formatIST(item.timestamp)}
                        </p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className={`font-bold text-sm ${isPositive ? "text-brand-primary" : "text-app-error"}`}>
                        {isPositive ? "+" : "-"}{formatUSD(item.displayAmount)}
                      </p>
                      {isWallet && item.transferId && (
                        <p className="text-[9px] text-app-textSecondary dark:text-gray-600 font-mono mt-0.5">
                          ID: {item.transferId.substring(0, 8)}...
                        </p>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          ) : (
            <div className="py-32 flex flex-col items-center justify-center text-center space-y-4">
              <div className="bg-app-bg dark:bg-gray-900/50 p-6 rounded-full">
                <History className="w-12 h-12 text-app-textSecondary dark:text-gray-700 opacity-30" />
              </div>
              <div className="space-y-1">
                <p className="text-app-textPrimary dark:text-white font-bold">No activities found</p>
                <p className="text-app-textSecondary text-sm max-w-xs">
                  {filterType === "ALL" 
                    ? "Start trading or add funds to your wallet to see your activity history here."
                    : `No ${filterType.toLowerCase()} activities match your current filters.`}
                </p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Activity;
