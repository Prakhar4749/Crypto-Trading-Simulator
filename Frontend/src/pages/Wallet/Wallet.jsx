import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  CopyIcon,
  ReloadIcon,
  ShuffleIcon,
  UpdateIcon,
  UploadIcon,
  DownloadIcon,
} from "@radix-ui/react-icons";
import { WalletIcon, TrendingUp, TrendingDown } from "lucide-react";
import { useEffect } from "react";
import TopupForm from "./TopupForm";
import TransferForm from "./TransferForm";
import WithdrawForm from "./WithdrawForm";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import SpinnerBackdrop from "@/components/custom/SpinnerBackdrop";
import { useWallet } from "@/contexts/WalletContext";
import { useWithdrawal } from "@/contexts/WithdrawalContext";
import { useAuth } from "@/contexts/AuthContext";
import { Button } from "@/components/ui/button";

import { formatUSD } from "@/Util/currencyUtils";
import { Badge } from "@/components/ui/badge";

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

const Wallet = () => {
  const navigate = useNavigate();
  const { userWallet, transactions, loading, getUserWallet, getWalletTransactions, topUpWallet } = useWallet();
  const { getPaymentDetails } = useWithdrawal();
  const { jwt } = useAuth();
  
  const query = useQuery();
  const paymentId = query.get("payment_id");
  const razorpayPaymentId = query.get("razorpay_payment_id");
  const orderId = query.get("order_id");
  const { order_id } = useParams();

  useEffect(() => {
    const currentJwt = jwt || localStorage.getItem("jwt");
    if (orderId || order_id) {
      topUpWallet(currentJwt, {
        orderId: orderId || order_id,
        paymentId: razorpayPaymentId || "AuedkfeuUe",
      }).then(() => {
        navigate("/wallet");
      });
    }
  }, [paymentId, orderId, razorpayPaymentId]);

  useEffect(() => {
    console.log("[Wallet] mounted, loading wallet data");
    handleFetchUserWallet();
    hanldeFetchWalletTransactions();
    getPaymentDetails(jwt || localStorage.getItem("jwt"));
  }, []);

  const handleFetchUserWallet = () => {
    getUserWallet(jwt || localStorage.getItem("jwt"));
  };

  const hanldeFetchWalletTransactions = () => {
    getWalletTransactions(jwt || localStorage.getItem("jwt"));
  };

  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text).then(() => {
      console.log("Wallet ID copied");
    });
  };

  if (loading) {
    console.log("[Wallet] waiting for wallet data to load");
    return <SpinnerBackdrop />;
  }

  if (!userWallet) {
    console.log("[Wallet] userWallet is null, showing empty state");
    return (
      <div className="flex items-center justify-center min-h-screen bg-app-bg">
        <p className="text-app-textSecondary font-medium">No wallet data found.</p>
      </div>
    );
  }

  return (
    <div className="bg-app-bg min-h-screen p-6">
      <div className="max-w-4xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-app-textPrimary font-bold text-2xl">My Digital Wallet</h1>
          <Button 
            variant="ghost" 
            size="icon" 
            onClick={handleFetchUserWallet}
            className="text-app-textSecondary hover:text-brand-primary"
          >
            <ReloadIcon className="w-5 h-5" />
          </Button>
        </div>

        {/* Wallet Balance Card */}
        <div className="bg-brand-primary rounded-card p-6 text-white shadow-cardHover relative overflow-hidden">
          <div className="absolute top-0 right-0 p-8 opacity-10">
            <WalletIcon size={100} />
          </div>
          
          <div className="relative z-10 space-y-4">
            <div className="space-y-1">
              <div className="flex items-center gap-2 text-white/70 text-sm font-medium">
                <span>Wallet ID: #FAVHJY{String(userWallet?.id ?? '')?.slice(-6)}</span>
                <CopyIcon 
                  onClick={() => copyToClipboard(String(userWallet?.id ?? ''))}
                  className="w-3.5 h-3.5 cursor-pointer hover:text-white" 
                />
              </div>
              <div className="flex items-center gap-3">
                <p className="text-white font-bold text-4xl mt-1">
                  {formatUSD(userWallet?.balance)}
                </p>
                <Badge className="bg-white/20 hover:bg-white/30 text-white border-none text-[10px] font-bold py-1 backdrop-blur-sm">
                  SIMULATOR
                </Badge>
              </div>
            </div>

            <div className="flex flex-wrap gap-3 pt-2">
              <Dialog>
                <DialogTrigger asChild>
                  <Button className="bg-white/20 hover:bg-white/30 text-white border-none rounded-input px-4 py-2 text-sm font-medium backdrop-blur-sm h-auto">
                    <UploadIcon className="mr-2 w-4 h-4" /> Top Up
                  </Button>
                </DialogTrigger>
                <DialogContent className="max-w-md p-0 overflow-hidden border-none shadow-card">
                  <div className="p-6">
                    <DialogHeader className="mb-4">
                      <DialogTitle className="text-app-textPrimary font-bold text-xl">Top Up Wallet</DialogTitle>
                    </DialogHeader>
                    <TopupForm />
                  </div>
                </DialogContent>
              </Dialog>

              <Dialog>
                <DialogTrigger asChild>
                  <Button className="bg-white/20 hover:bg-white/30 text-white border-none rounded-input px-4 py-2 text-sm font-medium backdrop-blur-sm h-auto">
                    <DownloadIcon className="mr-2 w-4 h-4" /> Withdraw
                  </Button>
                </DialogTrigger>
                <DialogContent className="max-w-md p-0 overflow-hidden border-none shadow-card">
                  <div className="p-6">
                    <DialogHeader className="mb-4">
                      <DialogTitle className="text-app-textPrimary font-bold text-xl">Withdraw Funds</DialogTitle>
                    </DialogHeader>
                    <WithdrawForm />
                  </div>
                </DialogContent>
              </Dialog>

              <Dialog>
                <DialogTrigger asChild>
                  <Button className="bg-white/20 hover:bg-white/30 text-white border-none rounded-input px-4 py-2 text-sm font-medium backdrop-blur-sm h-auto">
                    <ShuffleIcon className="mr-2 w-4 h-4" /> Transfer
                  </Button>
                </DialogTrigger>
                <DialogContent className="max-w-md p-0 overflow-hidden border-none shadow-card">
                  <div className="p-6">
                    <DialogHeader className="mb-4">
                      <DialogTitle className="text-app-textPrimary font-bold text-xl">Transfer to Wallet</DialogTitle>
                    </DialogHeader>
                    <TransferForm />
                  </div>
                </DialogContent>
              </Dialog>
            </div>
          </div>
        </div>

        {/* Transaction History Section */}
        <div className="mt-8 space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-app-textPrimary font-bold text-lg">Transaction History</h2>
            <Button 
              variant="ghost" 
              size="sm" 
              onClick={hanldeFetchWalletTransactions}
              className="text-brand-primary hover:bg-brand-light text-xs font-bold uppercase tracking-wider"
            >
              Refresh <UpdateIcon className="ml-2 h-3 w-3" />
            </Button>
          </div>

          <div className="bg-white rounded-card shadow-card border border-app-border overflow-hidden">
            {transactions?.length > 0 ? (
              <div className="divide-y divide-app-border">
                {transactions.map((item, index) => (
                  <div 
                    key={index} 
                    className="flex items-center justify-between px-6 py-4 border-b border-app-border last:border-0 hover:bg-app-bg transition-colors"
                  >
                    <div className="flex items-center gap-4">
                      <div className="bg-brand-light rounded-full p-2.5">
                        {item.amount > 0 ? (
                          <TrendingUp className="w-5 h-5 text-brand-primary" />
                        ) : (
                          <TrendingDown className="w-5 h-5 text-app-error" />
                        )}
                      </div>
                      <div className="space-y-0.5">
                        <p className="text-app-textPrimary font-medium text-sm">
                          {item.type || item.purpose || "Transaction"}
                        </p>
                        <p className="text-app-textSecondary text-xs">{item.date}</p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className={`font-semibold ${item.amount > 0 ? "text-brand-primary" : "text-app-error"}`}>
                        {item.amount > 0 ? "+" : ""}{formatUSD(item.amount)}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="py-20 flex flex-col items-center justify-center text-center space-y-2">
                <div className="bg-app-bg p-4 rounded-full">
                  <WalletIcon className="w-8 h-8 text-app-textSecondary opacity-40" />
                </div>
                <p className="text-app-textSecondary text-sm font-medium">No transactions yet</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Wallet;
