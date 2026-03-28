import { readableTimestamp } from "@/Util/readableTimestamp";
import { Badge } from "@/components/ui/badge";
import { useEffect, useState } from "react";
import { useWithdrawal } from "@/contexts/WithdrawalContext";
import { useAuth } from "@/contexts/AuthContext";
import { useWallet } from "@/contexts/WalletContext";
import { BanknoteIcon, Landmark, AlertCircle, Info, History } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { formatUSD, formatInr, usdToInr } from "@/Util/currencyUtils";
import { maskAccountNumber } from "@/Util/maskAccountNumber";
import { useNavigate } from "react-router-dom";

const Withdrawal = () => {
  const { history, paymentDetails, loading, getWithdrawalHistory, getPaymentDetails, withdrawalRequest } = useWithdrawal();
  const { userWallet, getUserWallet } = useWallet();
  const { jwt } = useAuth();
  const navigate = useNavigate();

  const [amount, setAmount] = useState("");

  useEffect(() => {
    const currentJwt = jwt || localStorage.getItem("jwt");
    getWithdrawalHistory(currentJwt);
    getPaymentDetails(currentJwt);
    getUserWallet(currentJwt);
  }, []);

  const handleWithdrawSubmit = async () => {
    const currentJwt = jwt || localStorage.getItem("jwt");
    try {
      await withdrawalRequest(currentJwt, amount);
      setAmount("");
      // Refresh history and wallet
      getWithdrawalHistory(currentJwt);
      getUserWallet(currentJwt);
    } catch (error) {
      console.log("[Withdrawal] request failed", error.message);
    }
  };

  const isInsufficient = parseFloat(amount) > userWallet?.balance;

  return (
    <div className="bg-app-bg min-h-screen p-6">
      <div className="max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Section 1: Request Withdrawal */}
        <div className="lg:col-span-1 space-y-6">
          <div className="flex items-center gap-3 mb-2">
            <div className="bg-brand-light p-2 rounded-lg">
              <BanknoteIcon className="h-6 w-6 text-brand-primary" />
            </div>
            <h1 className="text-app-textPrimary font-bold text-2xl">Withdraw Funds</h1>
          </div>

          <div className="bg-white rounded-card shadow-card border border-app-border p-6 space-y-6">
            {/* Balance Display */}
            <div className="bg-app-bg border border-app-border rounded-input p-4 flex justify-between items-center">
              <div className="space-y-0.5">
                <p className="text-app-textSecondary text-[10px] font-bold uppercase tracking-wider">Available Balance</p>
                <p className="text-app-textPrimary font-bold text-xl">{formatUSD(userWallet?.balance)}</p>
              </div>
              <Badge className="bg-brand-light text-brand-primary border-none text-[10px] font-bold">SIMULATOR</Badge>
            </div>

            {/* Payment Details Check */}
            {!paymentDetails ? (
              <div className="bg-red-50 border border-red-100 rounded-input p-4 flex gap-3">
                <AlertCircle className="h-5 w-5 text-app-error flex-shrink-0" />
                <div className="space-y-2">
                  <p className="text-xs text-app-textPrimary font-bold">No Payment Details Found</p>
                  <p className="text-[10px] text-app-textSecondary leading-relaxed">
                    You must add your bank account details before you can request a withdrawal.
                  </p>
                  <Button 
                    variant="link" 
                    onClick={() => navigate("/payment-details")}
                    className="text-app-error p-0 h-auto text-[10px] font-bold uppercase underline"
                  >
                    Add Details Now
                  </Button>
                </div>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="space-y-2">
                  <label className="text-app-textSecondary text-[10px] font-bold uppercase tracking-wider">Amount to Withdraw (USD)</label>
                  <Input
                    type="number"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    className="w-full border border-app-border rounded-input px-4 py-6 text-2xl font-bold
                    focus:outline-none focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary
                    text-app-textPrimary placeholder:text-app-textSecondary bg-app-bg/30"
                    placeholder="0.00"
                  />
                  {isInsufficient && (
                    <p className="text-app-error text-[10px] font-bold uppercase tracking-tighter">Insufficient funds</p>
                  )}
                </div>

                <div className="bg-brand-light/30 border border-brand-primary/20 rounded-input p-3 flex gap-3">
                  <Info className="h-4 w-4 text-brand-primary flex-shrink-0" />
                  <p className="text-[10px] text-app-textSecondary leading-relaxed">
                    Withdrawing {formatUSD(amount || 0)} will convert to <span className="text-brand-primary font-bold">{formatInr(usdToInr(amount || 0))}</span> INR.
                  </p>
                </div>

                <div className="pt-2">
                  <p className="text-app-textSecondary text-[10px] font-bold uppercase tracking-wider mb-2">Withdrawing to</p>
                  <div className="flex items-center gap-3 border border-app-border bg-app-bg/30 p-3 rounded-input">
                    <Landmark className="h-4 w-4 text-brand-primary" />
                    <div className="flex-1 overflow-hidden">
                      <p className="text-app-textPrimary font-bold text-xs truncate">{paymentDetails?.bankName}</p>
                      <p className="text-app-textSecondary text-[10px]">{maskAccountNumber(paymentDetails?.accountNumber)}</p>
                    </div>
                  </div>
                </div>

                <Button
                  onClick={handleWithdrawSubmit}
                  disabled={!amount || amount <= 0 || isInsufficient || loading}
                  className="w-full bg-brand-primary hover:bg-brand-dark text-white font-bold py-6 rounded-input shadow-lg transition-all active:scale-[0.98] mt-2"
                >
                  {loading ? "Processing..." : "Request Withdrawal"}
                </Button>
              </div>
            )}
          </div>
        </div>

        {/* Section 2: Withdrawal History */}
        <div className="lg:col-span-2 space-y-6">
          <div className="flex items-center gap-3 mb-2">
            <div className="bg-brand-light p-2 rounded-lg">
              <History className="h-6 w-6 text-brand-primary" />
            </div>
            <h2 className="text-app-textPrimary font-bold text-2xl">Withdrawal History</h2>
          </div>

          <div className="bg-white rounded-card shadow-card border border-app-border overflow-hidden">
            {history?.length > 0 ? (
              <div className="divide-y divide-app-border">
                {history.map((item) => (
                  <div 
                    key={item.id} 
                    className="flex items-center justify-between px-6 py-4 hover:bg-app-bg transition-colors"
                  >
                    <div className="flex items-center gap-4">
                      <div className="bg-brand-light rounded-full p-2.5">
                        <BanknoteIcon className="w-5 h-5 text-brand-primary" />
                      </div>
                      <div className="space-y-0.5">
                        <p className="text-app-textPrimary font-medium text-sm">
                          Bank Withdrawal
                        </p>
                        <p className="text-app-textSecondary text-xs">{readableTimestamp(item?.date)}</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-8">
                      <div className="text-right">
                        <p className="text-app-textPrimary font-bold text-sm">
                          {formatUSD(item.amount)}
                        </p>
                        <p className="text-[10px] text-app-textSecondary uppercase font-bold tracking-tighter">
                          USD
                        </p>
                      </div>
                      <Badge 
                        className={`text-white px-3 py-1 text-[10px] font-bold uppercase rounded-pill border-none 
                        ${item.status === "PENDING" ? "bg-orange-500" : 
                          item.status === "SUCCESS" ? "bg-brand-primary" : 
                          "bg-app-error"}`}
                      >
                        {item.status}
                      </Badge>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="py-32 flex flex-col items-center justify-center text-center space-y-3">
                <div className="bg-app-bg p-6 rounded-full">
                  <History className="w-10 h-10 text-app-textSecondary opacity-30" />
                </div>
                <div className="space-y-1">
                  <p className="text-app-textPrimary font-bold">No Records Found</p>
                  <p className="text-app-textSecondary text-sm">Your withdrawal requests will appear here.</p>
                </div>
              </div>
            )}
          </div>
        </div>

      </div>
    </div>
  );
};

export default Withdrawal;
