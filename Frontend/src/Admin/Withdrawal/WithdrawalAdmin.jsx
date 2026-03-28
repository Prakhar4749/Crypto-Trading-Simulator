import { readableTimestamp } from "@/Util/readableTimestamp";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useEffect } from "react";
import { useWithdrawal } from "@/contexts/WithdrawalContext";
import { useAuth } from "@/contexts/AuthContext";
import { Landmark, User, Clock, Check, X, ShieldAlert } from "lucide-react";

const WithdrawalAdmin = () => {
  const { requests, getAllWithdrawalRequests, updateWithdrawalStatus } = useWithdrawal();
  const { jwt } = useAuth();

  useEffect(() => {
    getAllWithdrawalRequests(jwt || localStorage.getItem("jwt"));
  }, []);

  const handleProccedWithdrawal = (id, accept) => {
    updateWithdrawalStatus(jwt || localStorage.getItem("jwt"), id, accept);
  };

  return (
    <div className="bg-app-bg min-h-screen p-6">
      <div className="max-w-5xl mx-auto">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-4">
          <div>
            <h1 className="text-app-textPrimary font-bold text-2xl mb-2">Withdrawal Requests</h1>
            <p className="text-app-textSecondary text-sm">Review and manage user withdrawal applications</p>
          </div>
          <span className="bg-red-50 text-app-error rounded-pill px-4 py-1.5 text-xs font-bold flex items-center gap-2 border border-app-error/10 w-fit">
            <ShieldAlert className="w-3.5 h-3.5" /> ADMIN CONTROL PANEL
          </span>
        </div>

        <div className="space-y-4 mt-6">
          {requests?.length > 0 ? (
            requests.map((item) => (
              <div 
                key={item.id}
                className="bg-white rounded-card shadow-card border border-app-border p-5 hover:border-brand-primary/20 transition-all group"
              >
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
                  <div className="flex items-start gap-4">
                    <div className="bg-app-bg p-3 rounded-full text-app-textSecondary group-hover:text-brand-primary group-hover:bg-brand-light transition-colors">
                      <Landmark className="w-6 h-6" />
                    </div>
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="text-app-textPrimary font-bold text-lg">${item.amount.toLocaleString()}</span>
                        <span className="text-app-textSecondary text-[10px] font-bold uppercase tracking-widest">USD</span>
                      </div>
                      <div className="flex items-center gap-2 text-app-textSecondary text-sm">
                        <User className="w-3.5 h-3.5" />
                        <span className="text-app-textPrimary font-semibold">{item.user.fullName}</span>
                        <span className="text-xs">({item.user.email})</span>
                      </div>
                      <div className="flex items-center gap-2 text-app-textSecondary text-xs">
                        <Clock className="w-3.5 h-3.5" />
                        <span>{readableTimestamp(item?.date)}</span>
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center gap-4 self-end md:self-center">
                    {item.status === "PENDING" ? (
                      <>
                        <Badge className="bg-yellow-50 text-yellow-600 border border-yellow-200 rounded-pill px-2 py-0.5 text-xs font-bold">
                          PENDING
                        </Badge>
                        <div className="flex items-center gap-2 ml-2">
                          <Button
                            onClick={() => handleProccedWithdrawal(item.id, true)}
                            className="bg-brand-primary hover:bg-brand-dark text-white rounded-input px-4 py-1.5 h-9 text-sm font-bold shadow-sm transition-all active:scale-95 flex items-center gap-1"
                          >
                            <Check className="w-4 h-4" /> Approve
                          </Button>
                          <Button
                            onClick={() => handleProccedWithdrawal(item.id, false)}
                            className="bg-app-error hover:bg-red-600 text-white rounded-input px-4 py-1.5 h-9 text-sm font-bold shadow-sm transition-all active:scale-95 flex items-center gap-1"
                          >
                            <X className="w-4 h-4" /> Reject
                          </Button>
                        </div>
                      </>
                    ) : (
                      <Badge className={`rounded-pill px-4 py-1.5 text-[10px] font-bold uppercase tracking-wider ${
                        item.status === "SUCCESS" ? "bg-brand-light text-brand-primary border-brand-primary/20" : "bg-red-50 text-app-error border-app-error/20"
                      }`}>
                        {item.status}
                      </Badge>
                    )}
                  </div>
                </div>
              </div>
            ))
          ) : (
            <div className="bg-white rounded-card shadow-card border border-app-border py-20 flex flex-col items-center justify-center text-center">
              <div className="bg-app-bg p-6 rounded-full mb-4">
                <Clock className="w-12 h-12 text-app-textSecondary opacity-30" />
              </div>
              <h3 className="text-app-textPrimary font-bold text-xl">No Pending Requests</h3>
              <p className="text-app-textSecondary text-sm max-w-xs">All withdrawal applications have been processed.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default WithdrawalAdmin;
