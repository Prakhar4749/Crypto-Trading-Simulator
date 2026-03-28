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
  const { items, getUserWatchlist, addToWatchlist } = useWatchlist();
  const { jwt } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    console.log("[Watchlist] mounted, loading watchlist");
    getUserWatchlist(jwt || localStorage.getItem("jwt"));
  }, []);

  const handleRemoveFromWatchlist = (id) => {
    addToWatchlist(jwt || localStorage.getItem("jwt"), id);
  };

  return (
    <div className="bg-app-bg min-h-screen p-6">
      <div className="max-w-6xl mx-auto">
        <div className="flex items-center gap-3 mb-8">
          <div className="bg-brand-light p-2 rounded-lg">
            <Star className="h-6 w-6 text-brand-primary fill-brand-primary" />
          </div>
          <h1 className="text-app-textPrimary font-bold text-2xl">My Watchlist</h1>
        </div>

        {items?.length > 0 ? (
          <div className="bg-white rounded-card shadow-card border border-app-border overflow-hidden">
            <div className="overflow-x-auto">
              <Table>
              <TableHeader className="bg-app-bg">
                <TableRow className="hover:bg-transparent border-app-border">
                  <TableHead className="py-4 px-6 text-app-textSecondary text-xs font-semibold uppercase tracking-wide">Coin</TableHead>
                  <TableHead className="text-app-textSecondary text-xs font-semibold uppercase tracking-wide">Symbol</TableHead>
                  <TableHead className="text-app-textSecondary text-xs font-semibold uppercase tracking-wide hidden md:table-cell">Volume</TableHead>
                  <TableHead className="text-app-textSecondary text-xs font-semibold uppercase tracking-wide hidden lg:table-cell">Market Cap</TableHead>
                  <TableHead className="text-app-textSecondary text-xs font-semibold uppercase tracking-wide">24H</TableHead>
                  <TableHead className="text-app-textSecondary text-xs font-semibold uppercase tracking-wide">Price</TableHead>
                  <TableHead className="text-right px-6 text-app-textSecondary text-xs font-semibold uppercase tracking-wide">Action</TableHead>
                </TableRow>
              </TableHeader>

              <TableBody>
                {items.map((item) => (
                  <TableRow
                    key={item.id}
                    className="cursor-pointer border-app-border hover:bg-brand-light transition-colors"
                  >
                    <TableCell 
                      className="px-6 py-4"
                      onClick={() => navigate(`/market/${item.id}`)}
                    >
                      <div className="flex items-center gap-3">
                        <Avatar className="h-8 w-8 ring-1 ring-app-border">
                          <AvatarImage src={item.image} alt={item.symbol} />
                        </Avatar>
                        <span className="text-app-textPrimary font-semibold text-sm">{item.name}</span>
                      </div>
                    </TableCell>
                    <TableCell className="text-app-textSecondary text-xs font-medium uppercase">
                      {item.symbol}
                    </TableCell>
                    <TableCell className="text-app-textPrimary text-sm hidden md:table-cell">
                      ${item.total_volume?.toLocaleString()}
                    </TableCell>
                    <TableCell className="text-app-textPrimary text-sm hidden lg:table-cell">
                      ${item.market_cap?.toLocaleString()}
                    </TableCell>
                    <TableCell>
                      <span
                        className={`px-2 py-1 rounded-pill text-[10px] font-bold ${
                          item.market_cap_change_percentage_24h < 0
                            ? "bg-red-50 text-app-error"
                            : "bg-brand-light text-brand-primary"
                        }`}
                      >
                        {item.market_cap_change_percentage_24h?.toFixed(2)}%
                      </span>
                    </TableCell>
                    <TableCell className="text-app-textPrimary font-semibold text-sm">
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
                        className="text-app-error hover:bg-red-50 hover:text-app-error rounded-full h-9 w-9"
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
            </div>
          </div>
        ) : (
          <div className="bg-white rounded-card shadow-card border border-app-border flex flex-col items-center justify-center py-24 text-center">
            <div className="bg-app-bg p-6 rounded-full mb-6">
              <Bookmark className="h-12 w-12 text-app-textSecondary opacity-30" />
            </div>
            <h3 className="text-app-textPrimary font-bold text-xl mb-2">Your watchlist is empty</h3>
            <p className="text-app-textSecondary text-sm max-w-xs mb-8">
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
