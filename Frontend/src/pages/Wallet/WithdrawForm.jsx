import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useState } from "react";
import { DialogClose } from "@/components/ui/dialog";
import { maskAccountNumber } from "@/Util/maskAccountNumber";
import { useNavigate } from "react-router-dom";
import { useWallet } from "@/contexts/WalletContext";
import { useWithdrawal } from "@/contexts/WithdrawalContext";
import { useAuth } from "@/contexts/AuthContext";
import { Landmark, AlertCircle, Info } from "lucide-react";
import { formatUSD, formatInr, usdToInr } from "@/Util/currencyUtils";

const WithdrawForm = () => {
  const [amount, setAmount] = useState("");
  const { userWallet } = useWallet();
  const { paymentDetails, withdrawalRequest } = useWithdrawal();
  const { jwt } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setAmount(e.target.value);
  };

  const handleSubmit = () => {
    withdrawalRequest(jwt || localStorage.getItem("jwt"), amount);
  };

  if (!paymentDetails) {
    return (
      <div className="py-10 flex flex-col items-center justify-center text-center space-y-6">
        <div className="bg-red-50 p-4 rounded-full">
          <AlertCircle className="w-10 h-10 text-app-error" />
        </div>
        <div className="space-y-2">
          <p className="text-app-textPrimary font-bold text-lg">No Payment Method</p>
          <p className="text-app-textSecondary text-sm max-w-xs">
            You need to add your bank details before you can withdraw funds.
          </p>
        </div>
        <Button 
          onClick={() => navigate("/payment-details")}
          className="bg-brand-primary hover:bg-brand-dark text-white rounded-input px-8"
        >
          Add Payment Details
        </Button>
      </div>
    );
  }

  const isInsufficient = parseFloat(amount) > userWallet?.balance;

  return (
    <div className="space-y-6">
      <div className="bg-app-bg border border-app-border rounded-input p-4 flex justify-between items-center">
        <span className="text-app-textSecondary text-sm font-medium">Available Balance</span>
        <span className="text-app-textPrimary font-bold text-lg">{formatUSD(userWallet?.balance)}</span>
      </div>

      <div className="space-y-4">
        <h3 className="text-app-textPrimary font-semibold text-base mb-2">
          Withdrawal Amount (USD)
        </h3>
        <Input
          onChange={handleChange}
          value={amount}
          type="number"
          className="w-full border border-app-border rounded-input px-4 py-6 text-2xl font-bold text-center
          focus:outline-none focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary
          text-app-textPrimary placeholder:text-app-textSecondary bg-transparent"
          placeholder="0.00"
        />
        {isInsufficient && (
          <p className="text-app-error text-[10px] font-bold text-center uppercase tracking-tighter">
            Insufficient funds in wallet
          </p>
        )}
      </div>

      <div className="bg-brand-light/30 border border-brand-primary/20 rounded-input p-4 flex gap-3">
        <Info className="h-5 w-5 text-brand-primary flex-shrink-0 mt-0.5" />
        <div className="space-y-1">
          <p className="text-xs text-app-textPrimary font-bold uppercase tracking-wider">Simulator Currency Note</p>
          <p className="text-[10px] text-app-textSecondary leading-relaxed">
            Withdrawing {formatUSD(amount || 0)} will convert to <span className="text-brand-primary font-bold">{formatInr(usdToInr(amount || 0))}</span> INR 
            based on the current simulator exchange rate.
          </p>
        </div>
      </div>

      <div className="space-y-3">
        <h3 className="text-app-textPrimary font-semibold text-base mb-2">
          Withdraw to
        </h3>
        <div className="flex items-center gap-4 border border-app-border bg-app-bg/30 p-4 rounded-input">
          <div className="bg-brand-light p-2 rounded-full">
            <Landmark className="h-5 w-5 text-brand-primary" />
          </div>
          <div className="flex-1 overflow-hidden">
            <p className="text-app-textPrimary font-bold text-sm truncate">
              {paymentDetails?.bankName}
            </p>
            <p className="text-app-textSecondary text-xs">
              Account: {maskAccountNumber(paymentDetails?.accountNumber)}
            </p>
          </div>
        </div>
      </div>

      <DialogClose asChild>
        <Button
          onClick={handleSubmit}
          disabled={!amount || amount <= 0 || isInsufficient}
          className="w-full bg-brand-primary hover:bg-brand-dark text-white font-bold py-7 rounded-input shadow-lg transition-all active:scale-[0.98] mt-2"
        >
          Withdraw {formatUSD(amount || 0)}
        </Button>
      </DialogClose>
    </div>
  );
};

export default WithdrawForm;
