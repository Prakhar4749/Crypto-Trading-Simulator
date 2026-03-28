import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";

import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Label } from "@/components/ui/label";

import * as yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import { useAuth } from "@/contexts/AuthContext";
import { useWithdrawal } from "@/contexts/WithdrawalContext";
import { useEffect } from "react";

const formSchema = yup.object().shape({
  accountHolderName: yup.string().required("Account holder name is required"),
  ifscCode: yup.string().length(11, "IFSC code must be 11 characters"),
  accountNumber: yup.string().required("Account number is required"),
  confirmAccountNumber: yup.string().test({
    name: "match",
    message: "Account numbers do not match",
    test: function (value) {
      return value === this.parent.accountNumber;
    },
  }),
  bankName: yup.string().required("Bank name is required"),
});

const PaymentDetailsForm = () => {
  const { user, jwt, loading: authLoading } = useAuth();
  const { addPaymentDetails, loading: withdrawalLoading } = useWithdrawal();

  useEffect(() => {
    console.log("[PaymentDetailsForm] user", { userId: user?.id });
  }, [user]);

  const form = useForm({
    resolver: yupResolver(formSchema),
    defaultValues: {
      accountHolderName: "",
      ifsc: "",
      accountNumber: "",
      bankName: "",
    },
  });
  const onSubmit = (data) => {
    addPaymentDetails(jwt || localStorage.getItem("jwt"), data);
    console.log("payment details form", data);
  };
  return (
    <div className="space-y-6 py-2">
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <FormField
            control={form.control}
            name="accountHolderName"
            render={({ field }) => (
              <FormItem>
                <Label className="text-app-textSecondary text-xs font-semibold uppercase tracking-wider">Account holder name</Label>
                <FormControl>
                  <Input
                    {...field}
                    className="w-full border border-app-border rounded-input px-4 py-3 
                    focus:outline-none focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary
                    text-app-textPrimary placeholder:text-app-textSecondary bg-transparent"
                    placeholder="e.g. CoinDesk User"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <div className="grid grid-cols-2 gap-4">
            <FormField
              control={form.control}
              name="bankName"
              render={({ field }) => (
                <FormItem>
                  <Label className="text-app-textSecondary text-xs font-semibold uppercase tracking-wider">Bank Name</Label>
                  <FormControl>
                    <Input
                      {...field}
                      className="w-full border border-app-border rounded-input px-4 py-3 
                      focus:outline-none focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary
                      text-app-textPrimary placeholder:text-app-textSecondary bg-transparent"
                      placeholder="e.g. YES Bank"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="ifsc"
              render={({ field }) => (
                <FormItem>
                  <Label className="text-app-textSecondary text-xs font-semibold uppercase tracking-wider">IFSC Code</Label>
                  <FormControl>
                    <Input
                      {...field}
                      className="w-full border border-app-border rounded-input px-4 py-3 
                      focus:outline-none focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary
                      text-app-textPrimary placeholder:text-app-textSecondary bg-transparent uppercase"
                      placeholder="YESB0000009"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <FormField
            control={form.control}
            name="accountNumber"
            render={({ field }) => (
              <FormItem>
                <Label className="text-app-textSecondary text-xs font-semibold uppercase tracking-wider">Account Number</Label>
                <FormControl>
                  <Input
                    {...field}
                    type="password"
                    className="w-full border border-app-border rounded-input px-4 py-3 
                    focus:outline-none focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary
                    text-app-textPrimary placeholder:text-app-textSecondary bg-transparent"
                    placeholder="*********5602"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="confirmAccountNumber"
            render={({ field }) => (
              <FormItem>
                <Label className="text-app-textSecondary text-xs font-semibold uppercase tracking-wider">Confirm Account Number</Label>
                <FormControl>
                  <Input
                    {...field}
                    className="w-full border border-app-border rounded-input px-4 py-3 
                    focus:outline-none focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary
                    text-app-textPrimary placeholder:text-app-textSecondary bg-transparent"
                    placeholder="Re-enter account number"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          {!(authLoading || withdrawalLoading) ? (
            <Button type="submit" className="w-full bg-brand-primary hover:bg-brand-dark text-white font-bold py-6 rounded-input shadow-lg transition-all active:scale-[0.98] mt-4">
              SAVE DETAILS
            </Button>
          ) : (
            <Skeleton className="w-full h-12 rounded-input mt-4" />
          )}
        </form>
      </Form>
    </div>
  );
};

export default PaymentDetailsForm;
