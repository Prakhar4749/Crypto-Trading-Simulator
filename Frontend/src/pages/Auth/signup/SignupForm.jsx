/* eslint-disable no-unused-vars */
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import { useEffect } from "react";
import SpinnerBackdrop from "@/components/custom/SpinnerBackdrop";

const formSchema = z.object({
  fullName: z.string().min(2, "Full name must be at least 2 characters"),
  email: z.string().email("Invalid email address"),
  password: z.string().min(8, "Password must be at least 8 characters long"),
});

const SignupForm = () => {
  const { register: authRegister, loading } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    console.log("[SignupForm] mounted");
  }, []);

  const form = useForm({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
      password: "",
      fullName: "",
    },
  });

  const onSubmit = (data) => {
    data.navigate = navigate;
    authRegister(data);
    console.log("signup form", data);
  };

  return (
    <div className="space-y-6 w-full">
      <div className="text-center space-y-1">
        <h2 className="text-app-textPrimary font-semibold text-xl">Create Account</h2>
        <p className="text-app-textSecondary text-sm">Join us and start your trading journey</p>
      </div>

      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <FormField
            control={form.control}
            name="fullName"
            render={({ field }) => (
              <FormItem>
                <FormControl>
                  <Input
                    {...field}
                    type="text"
                    className="w-full border border-app-border rounded-input px-4 py-2.5 
                    focus:outline-none focus:ring-2 focus:ring-brand-primary focus:border-brand-primary
                    text-app-textPrimary placeholder:text-app-textSecondary bg-transparent"
                    placeholder="Full Name"
                  />
                </FormControl>
                <FormMessage className="text-app-error text-xs" />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="email"
            render={({ field }) => (
              <FormItem>
                <FormControl>
                  <Input
                    {...field}
                    className="w-full border border-app-border rounded-input px-4 py-2.5 
                    focus:outline-none focus:ring-2 focus:ring-brand-primary focus:border-brand-primary
                    text-app-textPrimary placeholder:text-app-textSecondary bg-transparent"
                    placeholder="Email Address"
                  />
                </FormControl>
                <FormMessage className="text-app-error text-xs" />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="password"
            render={({ field }) => (
              <FormItem>
                <FormControl>
                  <Input
                    {...field}
                    type="password"
                    className="w-full border border-app-border rounded-input px-4 py-2.5 
                    focus:outline-none focus:ring-2 focus:ring-brand-primary focus:border-brand-primary
                    text-app-textPrimary placeholder:text-app-textSecondary bg-transparent"
                    placeholder="Password (min. 8 characters)"
                  />
                </FormControl>
                <FormMessage className="text-app-error text-xs" />
              </FormItem>
            )}
          />
          <Button 
            disabled={loading}
            type="submit" 
            className="w-full bg-brand-primary hover:bg-brand-dark text-white 
            font-semibold py-2.5 rounded-input transition-colors duration-200"
          >
            {loading ? "Creating Account..." : "Create Account"}
          </Button>
        </form>
      </Form>
      {loading && <SpinnerBackdrop show={true} />}
    </div>
  );
};

export default SignupForm;
