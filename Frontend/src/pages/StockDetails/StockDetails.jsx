/* eslint-disable no-unused-vars */
import { Button } from "@/components/ui/button";
import {
  BookmarkFilledIcon,
  BookmarkIcon,
  DotIcon,
} from "@radix-ui/react-icons";
import StockChart from "./StockChart";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import TradingForm from "./TradingForm";
import { useParams } from "react-router-dom";
import { useEffect } from "react";
import { Avatar, AvatarImage } from "@/components/ui/avatar";
import { existInWatchlist } from "@/Util/existInWatchlist";
import SpinnerBackdrop from "@/components/custom/SpinnerBackdrop";
import { Star } from "lucide-react";
import { useCoins } from "@/contexts/CoinContext";
import { useWatchlist } from "@/contexts/WatchlistContext";
import { useAuth } from "@/contexts/AuthContext";
import { useWallet } from "@/contexts/WalletContext";

const StockDetails = () => {
  const { id } = useParams();
  const { coinDetails, loading, fetchCoinDetails } = useCoins();
  const { items, getUserWatchlist, addToWatchlist } = useWatchlist();
  const { jwt } = useAuth();
  const { getUserWallet } = useWallet();

  useEffect(() => {
    console.log("[StockDetails] mounted", { id });
    fetchCoinDetails({ coinId: id });
    getUserWatchlist();
    getUserWallet();
  }, [id]);

  const handleAddToWatchlist = () => {
    addToWatchlist(id);
  };

  const isInWatchlist = existInWatchlist(items, coinDetails);

  if (loading) {
    return <SpinnerBackdrop />;
  }

  return (
    <div className="bg-app-bg min-h-screen p-6">
      <div className="max-w-[1200px] mx-auto">
        <div className="bg-white rounded-card shadow-card border border-app-border p-6 mb-6">
          <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
            <div className="flex gap-5 items-center">
              <Avatar className="h-16 w-16 ring-2 ring-brand-light p-1 bg-white">
                <AvatarImage src={coinDetails?.image?.large} />
              </Avatar>
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <h1 className="text-app-textPrimary font-bold text-2xl">{coinDetails?.name}</h1>
                  <span className="text-app-textSecondary text-sm font-medium uppercase bg-app-bg px-2 py-0.5 rounded-md border border-app-border">
                    {coinDetails?.symbol}
                  </span>
                </div>
                <div className="flex items-baseline gap-3">
                  <p className="text-app-textPrimary font-bold text-3xl">
                    ${coinDetails?.market_data.current_price.usd.toLocaleString()}
                  </p>
                  <p
                    className={`text-sm font-bold px-2 py-0.5 rounded-pill ${
                      coinDetails?.market_data.price_change_percentage_24h < 0
                        ? "bg-red-50 text-app-error"
                        : "bg-brand-light text-brand-primary"
                    }`}
                  >
                    {coinDetails?.market_data.price_change_percentage_24h?.toFixed(2)}%
                  </p>
                </div>
              </div>
            </div>

            <div className="flex items-center gap-3 w-full md:w-auto">
              <Button
                onClick={handleAddToWatchlist}
                variant={isInWatchlist ? "default" : "outline"}
                className={`flex-1 md:flex-none rounded-input px-6 h-11 font-medium transition-all shadow-sm active:scale-95 ${
                  isInWatchlist 
                    ? "bg-brand-primary text-white border-brand-primary hover:bg-brand-dark" 
                    : "bg-white dark:bg-transparent text-app-textSecondary dark:text-gray-400 border-app-border dark:border-gray-700 hover:bg-app-bg dark:hover:bg-gray-900"
                }`}
              >
                {isInWatchlist ? (
                  <>
                    <Star className="h-4 w-4 mr-2 fill-white" />
                    In Watchlist
                  </>
                ) : (
                  <>
                    <Star className="h-4 w-4 mr-2" />
                    Add to Watchlist
                  </>
                )}
              </Button>

              <Dialog>
                <DialogTrigger asChild>
                  <Button className="flex-1 md:flex-none bg-brand-primary hover:bg-brand-dark text-white font-bold h-11 px-8 rounded-input">
                    TRADE
                  </Button>
                </DialogTrigger>
                <DialogContent className="p-0 border-none bg-transparent shadow-none max-w-md">
                  <TradingForm />
                </DialogContent>
              </Dialog>
            </div>
          </div>
        </div>

        <div className="space-y-6">
          <StockChart coinId={coinDetails?.id} />
        </div>
      </div>
    </div>
  );
};

export default StockDetails;
