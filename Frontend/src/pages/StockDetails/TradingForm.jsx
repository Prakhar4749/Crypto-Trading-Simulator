import { Avatar, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { DialogClose } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Wallet, Info, ArrowRightLeft, Maximize2 } from "lucide-react";
import { useEffect, useState } from "react";
import { useCoins } from "@/contexts/CoinContext";
import { useAssets } from "@/contexts/AssetsContext";
import { useWallet } from "@/contexts/WalletContext";
import { useOrder } from "@/contexts/OrderContext";
import { showToast } from "@/utils/toast";

const TradingForm = () => {
  const { coinDetails } = useCoins();
  const { assetDetails, getAssetByUserIdAndCoinId } = useAssets();
  const { userWallet, getUserWallet } = useWallet();
  const { payOrder } = useOrder();
  
  const [orderType, setOrderType] = useState("BUY");
  const [inputMode, setInputMode] = useState("USD"); // "USD" or "CRYPTO"
  
  const [amount, setAmount] = useState("");
  const [quantity, setQuantity] = useState("");
  const [loading, setLoading] = useState(false);

  const currentPrice = coinDetails?.market_data?.current_price?.usd || 0;

  useEffect(() => {
    if (coinDetails?.id) {
      getAssetByUserIdAndCoinId(coinDetails.id);
      getUserWallet();
    }
  }, [coinDetails?.id]);

  const handleAmountChange = (e) => {
    const val = e.target.value;
    setAmount(val);
    if (!val || isNaN(val) || currentPrice === 0) {
      setQuantity("");
      return;
    }
    const calculatedQty = (parseFloat(val) / currentPrice).toFixed(8);
    setQuantity(calculatedQty);
  };

  const handleQuantityChange = (e) => {
    const val = e.target.value;
    setQuantity(val);
    if (!val || isNaN(val) || currentPrice === 0) {
      setAmount("");
      return;
    }
    const calculatedAmount = (parseFloat(val) * currentPrice).toFixed(2);
    setAmount(calculatedAmount);
  };

  const handleMax = () => {
    if (orderType === "SELL") {
      const maxQty = assetDetails?.quantity || 0;
      setQuantity(maxQty.toString());
      setAmount((maxQty * currentPrice).toFixed(2));
      setInputMode("CRYPTO");
    } else {
      const maxUSD = userWallet?.balance || 0;
      setAmount(maxUSD.toString());
      setQuantity((maxUSD / currentPrice).toFixed(8));
      setInputMode("USD");
    }
  };

  const toggleInputMode = () => {
    setInputMode(prev => prev === "USD" ? "CRYPTO" : "USD");
  };

  const handleOrder = async () => {
    if (!quantity || Number(quantity) <= 0) {
      showToast.error("Invalid input", "Please enter an amount to trade");
      return;
    }

    setLoading(true);
    try {
      await payOrder({
        coinId: coinDetails.id,
        quantity: Number(quantity),
        orderType: orderType
      });

      showToast.success(
        `${orderType} Order Successful`,
        `You ${orderType === 'BUY' ? 'bought' : 'sold'} ${quantity} ${coinDetails.symbol.toUpperCase()}`
      );

      // Refresh data
      await getUserWallet();
      await getAssetByUserIdAndCoinId(coinDetails.id);
      setAmount("");
      setQuantity("");

    } catch (error) {
      // Toast shown by context
    } finally {
      setLoading(false);
    }
  };

  const isInsufficientBalance = orderType === "BUY" && (parseFloat(amount) > (userWallet?.balance || 0));
  const isInsufficientQuantity = orderType === "SELL" && (parseFloat(quantity) > (assetDetails?.quantity || 0));

  return (
    <div className="bg-white dark:bg-[#1a1a2e] rounded-card shadow-card border border-app-border dark:border-gray-800 overflow-hidden w-full max-w-md">
      <div className="p-6 space-y-6">
        <div className="flex items-center justify-between mb-2">
          <div className="flex items-center gap-3">
            <Avatar className="h-8 w-8">
              <AvatarImage src={coinDetails?.image?.small} />
            </Avatar>
            <h2 className="text-app-textPrimary dark:text-white font-bold text-lg">Trade {coinDetails?.name}</h2>
          </div>
          <div className="flex items-center gap-1.5 bg-brand-light dark:bg-brand-dark/20 px-3 py-1 rounded-pill border border-brand-primary/10">
            <Info className="h-3 w-3 text-brand-primary" />
            <span className="text-[10px] font-bold text-brand-primary uppercase tracking-wider">Market</span>
          </div>
        </div>

        {/* Order Type Tabs */}
        <div className="flex p-1 bg-app-bg dark:bg-gray-900/50 border border-app-border dark:border-gray-800 rounded-input">
          <button
            onClick={() => setOrderType("BUY")}
            className={`flex-1 py-2.5 rounded-input font-bold text-sm transition-all ${
              orderType === "BUY" 
                ? "bg-brand-primary text-white shadow-md scale-[1.02]" 
                : "text-app-textSecondary dark:text-gray-400 hover:bg-brand-light dark:hover:bg-gray-800"
            }`}
          >
            BUY
          </button>
          <button
            onClick={() => setOrderType("SELL")}
            className={`flex-1 py-2.5 rounded-input font-bold text-sm transition-all ${
              orderType === "SELL" 
                ? "bg-app-error text-white shadow-md scale-[1.02]" 
                : "text-app-textSecondary dark:text-gray-400 hover:bg-red-50 dark:hover:bg-red-950/20"
            }`}
          >
            SELL
          </button>
        </div>

        {/* Input Section */}
        <div className="space-y-4">
          <div className="space-y-3">
            <div className="flex justify-between items-center">
              <div className="flex items-center gap-2">
                <label className="text-app-textSecondary dark:text-gray-400 text-[10px] font-bold uppercase tracking-widest">
                  {inputMode === "USD" ? "Enter Amount" : "Enter Quantity"}
                </label>
                <button 
                  onClick={toggleInputMode}
                  className="p-1 hover:bg-app-bg dark:hover:bg-gray-800 rounded-md transition-colors text-brand-primary"
                  title="Switch Input Mode"
                >
                  <ArrowRightLeft className="h-3 w-3" />
                </button>
              </div>
              <div className="flex items-center gap-2">
                <div className="flex items-center gap-1 text-[10px]">
                  <Wallet className="h-3 w-3 text-app-textSecondary" />
                  <span className="text-app-textSecondary dark:text-gray-500">Available:</span>
                  <span className="text-app-textPrimary dark:text-gray-300 font-bold">
                    {orderType === "BUY" 
                      ? `$${(userWallet?.balance || 0).toLocaleString()}` 
                      : `${(assetDetails?.quantity || 0).toFixed(4)} ${coinDetails?.symbol?.toUpperCase()}`}
                  </span>
                </div>
                <button 
                  onClick={handleMax}
                  className="text-[10px] font-bold text-brand-primary hover:text-brand-dark px-1.5 py-0.5 bg-brand-light dark:bg-brand-dark/20 rounded border border-brand-primary/20"
                >
                  MAX
                </button>
              </div>
            </div>

            <div className="relative group">
              {inputMode === "USD" ? (
                <Input
                  type="number"
                  value={amount}
                  onChange={handleAmountChange}
                  placeholder="0.00"
                  className="w-full border-2 border-app-border dark:border-gray-800 rounded-input px-4 py-7 text-2xl font-bold
                  focus:outline-none focus:ring-0 focus:border-brand-primary
                  text-app-textPrimary dark:text-white placeholder:text-app-textSecondary bg-app-bg/30 dark:bg-gray-900/30 transition-all"
                />
              ) : (
                <Input
                  type="number"
                  value={quantity}
                  onChange={handleQuantityChange}
                  placeholder="0.0000"
                  className="w-full border-2 border-app-border dark:border-gray-800 rounded-input px-4 py-7 text-2xl font-bold
                  focus:outline-none focus:ring-0 focus:border-brand-primary
                  text-app-textPrimary dark:text-white placeholder:text-app-textSecondary bg-app-bg/30 dark:bg-gray-900/30 transition-all"
                />
              )}
              <div className="absolute right-4 top-1/2 -translate-y-1/2 text-app-textSecondary dark:text-gray-500 font-black text-sm tracking-tighter">
                {inputMode === "USD" ? "USD" : coinDetails?.symbol?.toUpperCase()}
              </div>
            </div>
          </div>

          {/* Details & Calculations */}
          <div className="bg-app-bg dark:bg-gray-900/50 border border-app-border dark:border-gray-800 rounded-input p-4 space-y-3">
            <div className="flex justify-between items-center text-xs">
              <span className="text-app-textSecondary dark:text-gray-500 font-medium">
                {inputMode === "USD" ? "You Receive" : "Total Cost"}
              </span>
              <span className="text-app-textPrimary dark:text-white font-bold">
                {inputMode === "USD" 
                  ? `${quantity || '0.00'} ${coinDetails?.symbol?.toUpperCase()}`
                  : `$${(parseFloat(amount) || 0).toLocaleString()}`
                }
              </span>
            </div>
            <div className="flex justify-between items-center text-xs border-t border-app-border dark:border-gray-800 pt-2">
              <span className="text-app-textSecondary dark:text-gray-500 font-medium">Current Market Price</span>
              <span className="text-app-textPrimary dark:text-white font-bold">
                ${currentPrice.toLocaleString()}
              </span>
            </div>
          </div>

          {isInsufficientBalance && (
            <div className="text-app-error text-[10px] font-bold text-center bg-red-50 dark:bg-red-950/20 py-2 rounded-md border border-red-100 dark:border-red-900/30 flex items-center justify-center gap-2">
              <Info className="h-3 w-3" />
              INSUFFICIENT WALLET BALANCE
            </div>
          )}
          {isInsufficientQuantity && (
            <div className="text-app-error text-[10px] font-bold text-center bg-red-50 dark:bg-red-950/20 py-2 rounded-md border border-red-100 dark:border-red-900/30 flex items-center justify-center gap-2">
              <Info className="h-3 w-3" />
              INSUFFICIENT {coinDetails?.symbol?.toUpperCase()} IN PORTFOLIO
            </div>
          )}
        </div>

        {/* Action Button */}
        <div className="pt-2">
          <DialogClose asChild>
            <Button
              onClick={handleOrder}
              disabled={loading || !quantity || quantity <= 0 || isInsufficientBalance || isInsufficientQuantity}
              className={`w-full py-7 rounded-input text-base font-black transition-all shadow-lg active:scale-95 ${
                orderType === "BUY" 
                  ? "bg-brand-primary hover:bg-brand-dark text-white" 
                  : "bg-app-error hover:bg-red-600 text-white"
              } disabled:opacity-50 disabled:grayscale`}
            >
              {loading ? "PROCESSING..." : `CONFIRM ${orderType} ORDER`}
            </Button>
          </DialogClose>
        </div>
      </div>
    </div>
  );
};

export default TradingForm;
