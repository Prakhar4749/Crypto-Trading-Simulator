import { Avatar, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { DialogClose } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { DotIcon } from "@radix-ui/react-icons";
import { Wallet, Info } from "lucide-react";
import { useEffect, useState } from "react";
import { useCoins } from "@/contexts/CoinContext";
import { useAssets } from "@/contexts/AssetsContext";
import { useWallet } from "@/contexts/WalletContext";
import { useOrder } from "@/contexts/OrderContext";
import { useAuth } from "@/contexts/AuthContext";
import SpinnerBackdrop from "@/components/custom/SpinnerBackdrop";

const TradingForm = () => {
  const { coinDetails } = useCoins();
  const { assetDetails, loading: assetLoading, getAssetByUserIdAndCoinId } = useAssets();
  const { userWallet } = useWallet();
  const { payOrder } = useOrder();
  const { jwt } = useAuth();
  
  const [quantity, setQuantity] = useState(0);
  const [amount, setAmount] = useState("");
  const [orderType, setOrderType] = useState("BUY");

  useEffect(() => {
    const currentJwt = jwt || localStorage.getItem("jwt");
    if (coinDetails?.id) {
      getAssetByUserIdAndCoinId(currentJwt, coinDetails.id);
    }
  }, [coinDetails?.id]);

  const handleOnChange = (e) => {
    const val = e.target.value;
    setAmount(val);
    if (!val || isNaN(val)) {
      setQuantity(0);
      return;
    }
    const volume = calculateBuyCost(parseFloat(val), coinDetails.market_data.current_price.usd);
    setQuantity(volume);
  };

  function calculateBuyCost(amountUSD, cryptoPrice) {
    let volume = amountUSD / cryptoPrice;
    return volume.toFixed(8);
  }

  const handleOrder = async () => {
    const currentJwt = jwt || localStorage.getItem("jwt");
    const orderData = {
      coinId: coinDetails?.id,
      quantity: parseFloat(quantity),
      orderType,
    };
    console.log("[TradingForm] order submitted", orderData);
    await payOrder(currentJwt, orderData);
  };

  const isInsufficientBalance = orderType === "BUY" && (parseFloat(amount) > userWallet?.balance);
  const isInsufficientQuantity = orderType === "SELL" && (parseFloat(quantity) > (assetDetails?.quantity || 0));

  return (
    <div className="bg-white rounded-card shadow-card border border-app-border overflow-hidden w-full max-w-md">
      <div className="p-6 space-y-6">
        <div className="flex items-center justify-between mb-2">
          <h2 className="text-app-textPrimary font-bold text-lg">Trade {coinDetails?.name}</h2>
          <div className="flex items-center gap-1.5 bg-brand-light px-3 py-1 rounded-pill">
            <Info className="h-3.5 w-3.5 text-brand-primary" />
            <span className="text-[10px] font-bold text-brand-primary uppercase tracking-wider">Market Order</span>
          </div>
        </div>

        {/* Order Type Tabs */}
        <div className="flex p-1 bg-app-bg border border-app-border rounded-input">
          <button
            onClick={() => setOrderType("BUY")}
            className={`flex-1 py-2.5 rounded-input font-bold text-sm transition-all ${
              orderType === "BUY" 
                ? "bg-brand-primary text-white shadow-md scale-[1.02]" 
                : "text-app-textSecondary hover:bg-brand-light"
            }`}
          >
            BUY
          </button>
          <button
            onClick={() => setOrderType("SELL")}
            className={`flex-1 py-2.5 rounded-input font-bold text-sm transition-all ${
              orderType === "SELL" 
                ? "bg-app-error text-white shadow-md scale-[1.02]" 
                : "text-app-textSecondary hover:bg-red-50"
            }`}
          >
            SELL
          </button>
        </div>

        {/* Input Section */}
        <div className="space-y-4">
          <div className="space-y-2">
            <div className="flex justify-between items-end">
              <label className="text-app-textSecondary text-xs font-semibold uppercase tracking-wider">
                Amount in USD
              </label>
              <div className="flex items-center gap-1.5 text-xs">
                <Wallet className="h-3 w-3 text-app-textSecondary" />
                <span className="text-app-textSecondary">Available:</span>
                <span className="text-app-textPrimary font-bold">
                  {orderType === "BUY" 
                    ? `$${userWallet?.balance?.toLocaleString()}` 
                    : `${assetDetails?.quantity || 0} ${coinDetails?.symbol?.toUpperCase()}`}
                </span>
              </div>
            </div>
            <div className="relative">
              <Input
                type="number"
                value={amount}
                onChange={handleOnChange}
                placeholder="0.00"
                className="w-full border border-app-border rounded-input px-4 py-6 text-lg font-bold
                focus:outline-none focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary
                text-app-textPrimary placeholder:text-app-textSecondary bg-app-bg/30"
              />
              <div className="absolute right-4 top-1/2 -translate-y-1/2 text-app-textSecondary font-bold">
                USD
              </div>
            </div>
          </div>

          <div className="bg-app-bg border border-app-border rounded-input p-4 space-y-3">
            <div className="flex justify-between items-center text-sm">
              <span className="text-app-textSecondary font-medium">Estimated Quantity</span>
              <span className="text-app-textPrimary font-bold">
                {quantity} {coinDetails?.symbol?.toUpperCase()}
              </span>
            </div>
            <div className="flex justify-between items-center text-sm">
              <span className="text-app-textSecondary font-medium">Market Price</span>
              <span className="text-app-textPrimary font-bold">
                ${coinDetails?.market_data.current_price.usd.toLocaleString()}
              </span>
            </div>
          </div>

          {isInsufficientBalance && (
            <p className="text-app-error text-xs font-bold text-center bg-red-50 py-2 rounded-md border border-red-100">
              ⚠️ Insufficient wallet balance
            </p>
          )}
          {isInsufficientQuantity && (
            <p className="text-app-error text-xs font-bold text-center bg-red-50 py-2 rounded-md border border-red-100">
              ⚠️ Insufficient {coinDetails?.symbol?.toUpperCase()} in portfolio
            </p>
          )}
        </div>

        {/* Confirm Section */}
        <div className="pt-2">
          <DialogClose asChild>
            <Button
              onClick={handleOrder}
              disabled={!amount || amount <= 0 || isInsufficientBalance || isInsufficientQuantity}
              className={`w-full py-7 rounded-input text-base font-bold transition-all shadow-lg active:scale-[0.98] ${
                orderType === "BUY" 
                  ? "bg-brand-primary hover:bg-brand-dark text-white" 
                  : "bg-app-error hover:bg-red-600 text-white"
              }`}
            >
              CONFIRM {orderType}
            </Button>
          </DialogClose>
        </div>
      </div>
    </div>
  );
};

export default TradingForm;
