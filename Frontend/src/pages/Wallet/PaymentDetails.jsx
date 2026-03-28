import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import PaymentDetailsForm from "./PaymentDetailsForm";
import { useEffect } from "react";
import { maskAccountNumber } from "@/Util/maskAccountNumber";
import { useWithdrawal } from "@/contexts/WithdrawalContext";
import { useAuth } from "@/contexts/AuthContext";
import { Landmark } from "lucide-react";

const PaymentDetails = () => {
  const { paymentDetails, loading, getPaymentDetails } = useWithdrawal();
  const { jwt } = useAuth();

  useEffect(() => {
    getPaymentDetails(jwt || localStorage.getItem("jwt"));
  }, []);

  useEffect(() => {
    if (paymentDetails) {
      console.log("[PaymentDetails] loaded", { paymentDetails });
    }
  }, [paymentDetails]);

  return (
    <div className="bg-app-bg min-h-screen p-6">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-app-textPrimary font-bold text-2xl mb-6">Payment Details</h1>
        
        {paymentDetails ? (
          <div className="bg-white rounded-card shadow-card border border-app-border p-8 max-w-md">
            <div className="flex items-center gap-4 mb-6">
              <div className="bg-brand-light p-3 rounded-full">
                <Landmark className="h-6 w-6 text-brand-primary" />
              </div>
              <div>
                <h2 className="text-app-textPrimary font-bold text-xl uppercase tracking-tight">
                  {paymentDetails?.bankName}
                </h2>
                <p className="text-app-textSecondary text-xs font-medium uppercase tracking-wider">
                  Linked Bank Account
                </p>
              </div>
            </div>

            <div className="space-y-4 pt-4 border-t border-app-border">
              <div className="flex justify-between items-center">
                <p className="text-app-textSecondary text-sm">Account Number</p>
                <p className="text-app-textPrimary font-semibold">
                  {maskAccountNumber(paymentDetails?.accountNumber)}
                </p>
              </div>
              
              <div className="flex justify-between items-center">
                <p className="text-app-textSecondary text-sm">A/C Holder</p>
                <p className="text-app-textPrimary font-semibold">
                  {paymentDetails.accountHolderName}
                </p>
              </div>
              
              <div className="flex justify-between items-center">
                <p className="text-app-textSecondary text-sm">IFSC Code</p>
                <p className="text-app-textPrimary font-semibold uppercase">
                  {paymentDetails.ifsc}
                </p>
              </div>
            </div>

            <Dialog>
              <DialogTrigger asChild>
                <Button variant="outline" className="w-full mt-8 border-app-border text-app-textSecondary hover:text-brand-primary hover:border-brand-primary transition-all rounded-input">
                  Edit Details
                </Button>
              </DialogTrigger>
              <DialogContent className="max-w-md p-0 overflow-hidden border-none shadow-card">
                <div className="p-6">
                  <DialogHeader className="mb-4">
                    <DialogTitle className="text-app-textPrimary font-bold text-xl">Update Payment Details</DialogTitle>
                  </DialogHeader>
                  <PaymentDetailsForm />
                </div>
              </DialogContent>
            </Dialog>
          </div>
        ) : (
          <div className="bg-white rounded-card shadow-card border border-app-border p-12 text-center max-w-md">
            <div className="bg-app-bg p-4 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
              <Landmark className="w-8 h-8 text-app-textSecondary opacity-40" />
            </div>
            <h3 className="text-app-textPrimary font-bold text-lg mb-2">No Payment Method</h3>
            <p className="text-app-textSecondary text-sm mb-8">
              You haven't added any payment details yet. Please add your bank account to enable withdrawals.
            </p>
            <Dialog>
              <DialogTrigger asChild>
                <Button className="bg-brand-primary hover:bg-brand-dark text-white rounded-input px-8 py-6 h-auto font-bold shadow-lg transition-all active:scale-[0.98]">
                  Add Payment Details
                </Button>
              </DialogTrigger>
              <DialogContent className="max-w-md p-0 overflow-hidden border-none shadow-card">
                <div className="p-6">
                  <DialogHeader className="mb-4">
                    <DialogTitle className="text-app-textPrimary font-bold text-xl">Payment Details</DialogTitle>
                  </DialogHeader>
                  <PaymentDetailsForm />
                </div>
              </DialogContent>
            </Dialog>
          </div>
        )}
      </div>
    </div>
  );
};

export default PaymentDetails;
