import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import { useEffect, useState } from "react";
import { BadgeCheck, Mail, ShieldCheck, User, Calendar, Globe, MapPin, Hash, Lock, ShieldAlert, Phone, Clock } from "lucide-react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { useNavigate, useLocation } from "react-router-dom";
import EditProfileModal from "@/components/modals/EditProfileModal";
import KycStatusCard from "@/components/KycStatusCard";

const Profile = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [editOpen, setEditOpen] = useState(false);

  useEffect(() => {
    console.log("[Profile] mounted");
  }, []);

  const openVerification = (type) => {
    console.log("[Profile] opening verification", { type });
    navigate("/profile/verify", { 
      state: { 
        from: location.pathname,
        type: type 
      } 
    });
  };

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString('en-GB');
  };

  return (
    <div className="bg-app-bg dark:bg-[#0f0f1a] min-h-screen p-6 transition-colors">
      <div className="max-w-4xl mx-auto space-y-6">
        <h1 className="text-app-textPrimary dark:text-white font-bold text-2xl mb-6">User Profile</h1>

        <KycStatusCard onAction={() => setEditOpen(true)} />

        {/* Profile Header Card */}
        <div className="bg-white dark:bg-[#1a1a2e] rounded-card shadow-card border border-app-border dark:border-gray-800 p-6">
          <div className="flex flex-col md:flex-row items-center gap-8">
            <div className="relative">
              <Avatar className="w-20 h-20 rounded-full ring-4 ring-brand-light dark:ring-brand-dark/20">
                <AvatarImage src={user?.profilePicture} />
                <AvatarFallback className="bg-brand-light dark:bg-brand-dark/20 text-brand-primary text-2xl font-bold">
                  {user?.fullName?.charAt(0) || "U"}
                </AvatarFallback>
              </Avatar>
              {user?.verified && (
                <div className="absolute -bottom-1 -right-1 bg-white dark:bg-[#1a1a2e] rounded-full p-1 shadow-sm">
                  <BadgeCheck className="w-6 h-6 text-brand-primary fill-brand-light dark:fill-brand-dark/20" />
                </div>
              )}
            </div>
            
            <div className="flex-1 text-center md:text-left space-y-2">
              <div className="flex flex-col md:flex-row items-center gap-3">
                <h2 className="text-app-textPrimary dark:text-white font-bold text-xl">{user?.fullName || "CoinDesk User"}</h2>
                {user?.kycStatus === 'VERIFIED' ? (
                  <span className="bg-brand-light dark:bg-green-500/10 text-brand-primary dark:text-green-400 rounded-pill px-3 py-1 text-xs font-semibold flex items-center gap-1">
                    <BadgeCheck className="w-3.5 h-3.5" /> Verified
                  </span>
                ) : user?.kycStatus === 'PENDING' ? (
                  <span className="bg-amber-50 dark:bg-amber-500/10 text-amber-600 dark:text-amber-400 rounded-pill px-3 py-1 text-xs font-semibold flex items-center gap-1">
                    <Clock className="w-3.5 h-3.5" /> Pending
                  </span>
                ) : (
                  <span className="bg-red-50 dark:bg-red-500/10 text-app-error rounded-pill px-3 py-1 text-xs font-semibold flex items-center gap-1">
                    <ShieldAlert className="w-3.5 h-3.5" /> Unverified
                  </span>
                )}
              </div>
              <p className="text-app-textSecondary dark:text-gray-400 text-sm flex items-center justify-center md:justify-start gap-2">
                <Mail className="w-4 h-4" /> {user?.email}
              </p>
            </div>

            <Button 
              onClick={() => setEditOpen(true)}
              className="bg-brand-primary hover:bg-brand-dark text-white font-bold px-8 rounded-input shadow-lg transition-all active:scale-[0.98]"
            >
              Edit Profile
            </Button>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Personal Information */}
          <div className="bg-white dark:bg-[#1a1a2e] rounded-card shadow-card border border-app-border dark:border-gray-800 p-6 space-y-4">
            <div className="flex items-center gap-2 mb-2">
              <User className="w-5 h-5 text-brand-primary" />
              <h3 className="text-app-textPrimary dark:text-white font-bold text-lg">Personal Information</h3>
            </div>
            
            <div className="space-y-1">
              {[
                { label: "Phone", value: user?.phoneNumber || "N/A", icon: Phone },
                { label: "Address", value: user?.address || "N/A", icon: MapPin },
                { label: "City", value: user?.city || "N/A", icon: MapPin },
                { label: "Country", value: user?.country || "N/A", icon: Globe },
                { label: "Member Since", value: formatDate(user?.createdAt), icon: Calendar },
              ].map((item, idx, arr) => (
                <div key={idx} className={`flex justify-between border-b border-app-border dark:border-gray-800 py-3 ${idx === arr.length - 1 ? 'last:border-0 border-none' : ''}`}>
                  <span className="text-app-textSecondary dark:text-gray-400 text-sm flex items-center gap-2">
                    <item.icon className="w-3.5 h-3.5" /> {item.label}
                  </span>
                  <span className="text-app-textPrimary dark:text-white font-medium text-sm text-right max-w-[150px] truncate">{item.value}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Security & Verification */}
          <div className="bg-white dark:bg-[#1a1a2e] rounded-card shadow-card border border-app-border dark:border-gray-800 p-6 space-y-6">
            <div className="flex items-center gap-2 mb-2">
              <ShieldCheck className="w-5 h-5 text-brand-primary" />
              <h3 className="text-app-textPrimary dark:text-white font-bold text-lg">Account Security</h3>
            </div>

            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <p className="text-app-textPrimary dark:text-white font-semibold text-sm">2-Step Verification</p>
                  <p className="text-app-textSecondary dark:text-gray-400 text-xs">Secure your account with 2FA</p>
                </div>
                {user?.twoFactorAuth?.enabled ? (
                  <Badge className="bg-brand-light dark:bg-brand-dark/20 text-brand-primary dark:text-brand-primary border-none rounded-pill font-bold px-3">ENABLED</Badge>
                ) : (
                  <Badge className="bg-red-50 dark:bg-red-500/10 text-app-error border-none rounded-pill font-bold px-3">DISABLED</Badge>
                )}
              </div>

              {!user?.twoFactorAuth?.enabled && (
                <Button 
                  onClick={() => openVerification("2FA_SETUP")}
                  variant="outline" 
                  className="w-full border-brand-primary text-brand-primary dark:text-brand-primary hover:bg-brand-light dark:hover:bg-brand-dark/10 rounded-input font-bold"
                >
                  Enable 2FA
                </Button>
              )}

              <div className="pt-4 border-t border-app-border dark:border-gray-800 flex items-center justify-between">
                <div className="space-y-0.5">
                  <p className="text-app-textPrimary dark:text-white font-semibold text-sm">Account Status</p>
                  <p className="text-app-textSecondary dark:text-gray-400 text-xs">Identity verification status</p>
                </div>
                {user?.kycStatus !== 'VERIFIED' && (
                  <Button 
                    onClick={() => openVerification("ID_VERIFICATION")}
                    size="sm" 
                    className="bg-brand-primary hover:bg-brand-dark text-white rounded-input px-4 font-bold"
                  >
                    Verify Now
                  </Button>
                )}
              </div>

              <div className="pt-4 border-t border-app-border dark:border-gray-800 flex items-center justify-between">
                <div className="space-y-0.5">
                  <p className="text-app-textPrimary dark:text-white font-semibold text-sm">Change Password</p>
                  <p className="text-app-textSecondary dark:text-gray-400 text-xs">Update your security</p>
                </div>
                <Button variant="ghost" size="sm" className="text-brand-primary hover:bg-brand-light font-bold flex items-center gap-1">
                  <Lock className="w-3.5 h-3.5" /> Update
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <EditProfileModal 
        isOpen={editOpen} 
        onClose={() => setEditOpen(false)} 
      />
    </div>
  );
};

export default Profile;
