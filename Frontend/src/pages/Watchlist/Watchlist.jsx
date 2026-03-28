import { useEffect } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Avatar, AvatarImage } from "@/components/ui/avatar";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Trash2, Bookmark, Star } from "lucide-react";
import { useWatchlist } from "@/contexts/WatchlistContext";
import { useAuth } from "@/contexts/AuthContext";

const Watchlist = () => {
  const { items, getUserWatchlist, addToWatchlist, loading } = useWatchlist();
  const { jwt } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    console.log("[Watchlist] mounting, items currently:", items);
    getUserWatchlist();
  }, []);

  useEffect(() => {
    console.log("[Watchlist] items updated:", items);
  }, [items]);

  const handleRemoveFromWatchlist = (id) => {
    addToWatchlist(id);
  };

  return (
    <div className="bg-app-bg min-h-screen p-6">
      <div className="max-w-6xl mx-auto">
        <div className="flex items-center gap-3 mb-8">
          <div className="bg-brand-light dark:bg-brand-dark/20 p-2 rounded-lg">
            <Star className="h-6 w-6 text-brand-primary fill-brand-primary" />
          </div>
          <h1 className="text-app-textPrimary dark:text-white font-bold text-2xl">My Watchlist</h1>
        </div>

        {loading && !items?.length ? (
          <div className="py-24 flex justify-center">
            <div className="w-12 h-12 border-4 border-brand-primary border-t-transparent rounded-full animate-spin" />
          </div>
        ) : items?.length > 0 ? (
          <div className="bg-white dark:bg-[#1a1a2e] rounded-card shadow-card border border-app-border dark:border-gray-800 overflow-hidden">
            <div className="overflow-x-auto">
              <Table>
              <TableHeader className="bg-app-bg dark:bg-gray-900/50">
                <TableRow className="hover:bg-transparent border-app-border dark:border-gray-800">
                  <TableHead className="py-4 px-6 text-app-textSecondary dark:text-gray-400 text-xs font-semibold uppercase tracking-wide">Coin</TableHead>
                  <TableHead className="text-app-textSecondary dark:text-gray-400 text-xs font-semibold uppercase tracking-wide">Symbol</TableHead>
                  <TableHead className="text-app-textSecondary dark:text-gray-400 text-xs font-semibold uppercase tracking-wide hidden md:table-cell">Volume</TableHead>
                  <TableHead className="text-app-textSecondary dark:text-gray-400 text-xs font-semibold uppercase tracking-wide hidden lg:table-cell">Market Cap</TableHead>
                  <TableHead className="text-app-textSecondary dark:text-gray-400 text-xs font-semibold uppercase tracking-wide">24H</TableHead>
                  <TableHead className="text-app-textSecondary dark:text-gray-400 text-xs font-semibold uppercase tracking-wide">Price</TableHead>
                  <TableHead className="text-right px-6 text-app-textSecondary dark:text-gray-400 text-xs font-semibold uppercase tracking-wide">Action</TableHead>
                </TableRow>
              </TableHeader>

              <TableBody>
                {items.map((item, index) => {
                  // Fallback for key if item is just a string or object without id
                  const itemKey = (typeof item === 'string' ? item : item.id) || `watchlist-item-${index}`;
                  
                  // Handle case where item is just an ID (string) - might happen if Feign fetch fails
                  if (typeof item === 'string') {
                    return (
                      <TableRow key={itemKey} className="border-app-border dark:border-gray-800">
                        <TableCell className="px-6 py-4 font-medium text-brand-primary uppercase">
                          {item}
                        </TableCell>
                        <TableCell colSpan={5} className="text-app-textSecondary italic text-sm">
                          Details unavailable (click to view market)
                        </TableCell>
                        <TableCell className="text-right px-6">
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => handleRemoveFromWatchlist(item)}
                            className="text-app-error hover:bg-red-50 dark:hover:bg-red-900/20 rounded-full h-9 w-9"
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </TableCell>
                      </TableRow>
                    );
                  }

                  return (
                    <TableRow
                      key={itemKey}
                      className="cursor-pointer border-app-border dark:border-gray-800 hover:bg-brand-light dark:hover:bg-gray-900/30 transition-colors"
                    >
                      <TableCell 
                        className="px-6 py-4"
                        onClick={() => navigate(`/market/${item.id}`)}
                      >
                        <div className="flex items-center gap-3">
                          <Avatar className="h-8 w-8 ring-1 ring-app-border dark:ring-gray-700">
                            <AvatarImage src={item.image} alt={item.symbol} />
                          </Avatar>
                          <span className="text-app-textPrimary dark:text-white font-semibold text-sm">{item.name}</span>
                        </div>
                      </TableCell>
                      <TableCell className="text-app-textSecondary dark:text-gray-400 text-xs font-medium uppercase">
                        {item.symbol}
                      </TableCell>
                      <TableCell className="text-app-textPrimary dark:text-gray-300 text-sm hidden md:table-cell">
                        ${item.total_volume?.toLocaleString()}
                      </TableCell>
                      <TableCell className="text-app-textPrimary dark:text-gray-300 text-sm hidden lg:table-cell">
                        ${item.market_cap?.toLocaleString()}
                      </TableCell>
                      <TableCell>
                        <span
                          className={`px-2 py-1 rounded-pill text-[10px] font-bold ${
                            item.price_change_percentage_24h < 0
                              ? "bg-red-50 dark:bg-red-900/20 text-app-error"
                              : "bg-brand-light dark:bg-brand-dark/20 text-brand-primary"
                          }`}
                        >
                          {item.price_change_percentage_24h?.toFixed(2)}%
                        </span>
                      </TableCell>
                      <TableCell className="text-app-textPrimary dark:text-white font-semibold text-sm">
                        ${item.current_price?.toLocaleString()}
                      </TableCell>
                      <TableCell className="text-right px-6">
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleRemoveFromWatchlist(item.id);
                          }}
                          className="text-app-error hover:bg-red-50 dark:hover:bg-red-900/20 hover:text-app-error rounded-full h-9 w-9"
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
            </div>
          </div>
        ) : (
          <div className="bg-white dark:bg-[#1a1a2e] rounded-card shadow-card border border-app-border dark:border-gray-800 flex flex-col items-center justify-center py-24 text-center">
            <div className="bg-app-bg dark:bg-gray-900/50 p-6 rounded-full mb-6">
              <Bookmark className="h-12 w-12 text-app-textSecondary dark:text-gray-600 opacity-30" />
            </div>
            <h3 className="text-app-textPrimary dark:text-white font-bold text-xl mb-2">Your watchlist is empty</h3>
            <p className="text-app-textSecondary dark:text-gray-400 text-sm max-w-xs mb-8">
              Start following your favorite cryptocurrencies to keep track of their performance.
            </p>
            <Button 
              onClick={() => navigate("/")}
              className="bg-brand-primary hover:bg-brand-dark text-white font-bold px-8 h-12 rounded-input shadow-lg transition-all active:scale-[0.98]"
            >
              Explore Market
            </Button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Watchlist;
