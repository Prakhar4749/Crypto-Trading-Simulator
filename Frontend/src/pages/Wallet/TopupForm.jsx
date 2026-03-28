import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Skeleton } from "@/components/ui/skeleton";
import { DotFilledIcon } from "@radix-ui/react-icons";
import { useEffect, useState } from "react";
import { useWallet } from "@/contexts/WalletContext";
import { useAuth } from "@/contexts/AuthContext";

const TopupForm = () => {
  const [amount, setAmount] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("RAZORPAY");
  const { userWallet, paymentHandler, loading } = useWallet();
  const { jwt } = useAuth();

  useEffect(() => {
    console.log("[TopupForm] mounted");
  }, []);

  const handleChange = (e) => {
    setAmount(e.target.value);
  };

  const handleSubmit = () => {
    paymentHandler(jwt || localStorage.getItem("jwt"), amount, paymentMethod);
    console.log("Topup submitted", { amount, paymentMethod });
  };

  return (
    <div className="space-y-6">
      <div className="space-y-4">
        <h3 className="text-app-textPrimary font-semibold text-base mb-2">
          Enter Amount (USD)
        </h3>
        <Input
          onChange={handleChange}
          value={amount}
          type="number"
          className="w-full border border-app-border rounded-input px-4 py-6 text-2xl font-bold
          focus:outline-none focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary
          text-app-textPrimary placeholder:text-app-textSecondary bg-app-bg/30"
          placeholder="0.00"
        />
      </div>

      <div className="space-y-4">
        <h3 className="text-app-textPrimary font-semibold text-base mb-2">
          Select Payment Method
        </h3>
        <RadioGroup
          onValueChange={(value) => setPaymentMethod(value)}
          className="grid grid-cols-1 gap-4"
          defaultValue="RAZORPAY"
        >
          <div className={`relative flex items-center justify-center border rounded-input p-5 cursor-pointer transition-all border-brand-primary bg-brand-light/30 shadow-sm ring-1 ring-brand-primary/30`}>
            <RadioGroupItem
              value="RAZORPAY"
              id="razorpay"
              className="sr-only"
            />
            <Label htmlFor="razorpay" className="cursor-pointer w-full flex justify-center">
              <div className="flex items-center gap-3">
                <img
                  src="https://upload.wikimedia.org/wikipedia/commons/8/89/Razorpay_logo.svg"
                  alt="Razorpay"
                  className="h-6"
                />
              </div>
            </Label>
            <DotFilledIcon className="absolute top-2 right-2 text-brand-primary w-4 h-4" />
          </div>
        </RadioGroup>
      </div>
      {loading ? (
        <Skeleton className="h-14 w-full rounded-input" />
      ) : (
        <Button
          onClick={handleSubmit}
          disabled={!amount || amount <= 0}
          className="w-full bg-brand-primary hover:bg-brand-dark text-white font-bold py-7 rounded-input shadow-lg transition-all active:scale-[0.98] mt-2"
        >
          Proceed to Pay ${amount || "0"}
        </Button>
      )}
    </div>
  );
};

export default TopupForm;
