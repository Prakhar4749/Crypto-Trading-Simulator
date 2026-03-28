import { Button } from "@/components/ui/button";
import { DialogClose } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useState } from "react";
import { useWallet } from "@/contexts/WalletContext";
import { useAuth } from "@/contexts/AuthContext";
import { Info } from "lucide-react";

const TransferForm = () => {
  const { transferMoney } = useWallet();
  const { jwt } = useAuth();

  const [formData, setFormData] = useState({
    amount: "",
    walletId: "",
    purpose: "",
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async () => {
    const currentJwt = jwt || localStorage.getItem("jwt");
    try {
      await transferMoney(currentJwt, {
        walletId: formData.walletId,
        amount: formData.amount,
        purpose: formData.purpose,
      });
    } catch (error) {
      console.log("[TransferForm] transfer failed", error.message);
    }
  };

  return (
    <div className="space-y-6">
      <div className="bg-brand-light/30 border border-brand-primary/20 rounded-input p-4 flex gap-3">
        <Info className="h-5 w-5 text-brand-primary flex-shrink-0 mt-0.5" />
        <p className="text-xs text-app-textSecondary">
          Transfers are instant and irreversible. Please double-check the recipient's Wallet ID before sending.
        </p>
      </div>

      <div className="space-y-5">
        <div className="space-y-2">
          <h3 className="text-app-textPrimary font-semibold text-base mb-2">
            Recipient Wallet ID
          </h3>
          <Input
            name="walletId"
            onChange={handleChange}
            value={formData.walletId}
            className="w-full border border-app-border rounded-input px-4 py-3 
            focus:outline-none focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary
            text-app-textPrimary placeholder:text-app-textSecondary bg-transparent"
            placeholder="#ADFE34..."
          />
        </div>

        <div className="space-y-2">
          <h3 className="text-app-textPrimary font-semibold text-base mb-2">
            Amount (USD)
          </h3>
          <Input
            name="amount"
            type="number"
            onChange={handleChange}
            value={formData.amount}
            className="w-full border border-app-border rounded-input px-4 py-6 text-2xl font-bold
            focus:outline-none focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary
            text-app-textPrimary placeholder:text-app-textSecondary bg-app-bg/30"
            placeholder="0.00"
          />
        </div>

        <div className="space-y-2">
          <h3 className="text-app-textPrimary font-semibold text-base mb-2">
            Purpose / Note
          </h3>
          <Input
            name="purpose"
            onChange={handleChange}
            value={formData.purpose}
            className="w-full border border-app-border rounded-input px-4 py-3 
            focus:outline-none focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary
            text-app-textPrimary placeholder:text-app-textSecondary bg-transparent"
            placeholder="e.g. Gift, Payment..."
          />
        </div>
      </div>

      <DialogClose asChild>
        <Button
          onClick={handleSubmit}
          disabled={!formData.amount || !formData.walletId}
          className="w-full bg-brand-primary hover:bg-brand-dark text-white font-bold py-7 rounded-input shadow-lg transition-all active:scale-[0.98] mt-2"
        >
          Send ${formData.amount || "0"}
        </Button>
      </DialogClose>
    </div>
  );
};

export default TransferForm;
