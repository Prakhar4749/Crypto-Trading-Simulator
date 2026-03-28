import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import { useEffect } from "react";
import { BadgeCheck, Mail, ShieldCheck, User, Calendar, Globe, MapPin, Hash, Lock, ShieldAlert } from "lucide-react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { useNavigate, useLocation } from "react-router-dom";

const Profile = () => {
  const { user, jwt } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

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

  return (
    <div className="bg-app-bg min-h-screen p-6">
      <div className="max-w-4xl mx-auto space-y-6">
        <h1 className="text-app-textPrimary font-bold text-2xl mb-6">User Profile</h1>

        {/* Profile Header Card */}
        <div className="bg-white rounded-card shadow-card border border-app-border p-6">
          <div className="flex flex-col md:flex-row items-center gap-8">
            <div className="relative">
              <Avatar className="w-20 h-20 rounded-full ring-4 ring-brand-light">
                <AvatarImage src="https://github.com/shadcn.png" />
                <AvatarFallback className="bg-brand-light text-brand-primary text-2xl font-bold">
                  {user?.fullName?.charAt(0) || "U"}
                </AvatarFallback>
              </Avatar>
              {user?.verified && (
                <div className="absolute -bottom-1 -right-1 bg-white rounded-full p-1 shadow-sm">
                  <BadgeCheck className="w-6 h-6 text-brand-primary fill-brand-light" />
                </div>
              )}
            </div>
            
            <div className="flex-1 text-center md:text-left space-y-2">
              <div className="flex flex-col md:flex-row items-center gap-3">
                <h2 className="text-app-textPrimary font-bold text-xl">{user?.fullName || "CoinDesk User"}</h2>
                {user?.verified ? (
                  <span className="bg-brand-light text-brand-primary rounded-pill px-3 py-1 text-xs font-semibold flex items-center gap-1">
                    <BadgeCheck className="w-3.5 h-3.5" /> Verified
                  </span>
                ) : (
                  <span className="bg-red-50 text-app-error rounded-pill px-3 py-1 text-xs font-semibold flex items-center gap-1">
                    <ShieldAlert className="w-3.5 h-3.5" /> Unverified
                  </span>
                )}
              </div>
              <p className="text-app-textSecondary text-sm flex items-center justify-center md:justify-start gap-2">
                <Mail className="w-4 h-4" /> {user?.email}
              </p>
            </div>

            <Button className="bg-brand-primary hover:bg-brand-dark text-white font-bold px-8 rounded-input shadow-lg transition-all active:scale-[0.98]">
              Edit Profile
            </Button>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Personal Information */}
          <div className="bg-white rounded-card shadow-card border border-app-border p-6 space-y-4">
            <div className="flex items-center gap-2 mb-2">
              <User className="w-5 h-5 text-brand-primary" />
              <h3 className="text-app-textPrimary font-bold text-lg">Personal Information</h3>
            </div>
            
            <div className="space-y-1">
              {[
                { label: "Date of Birth", value: "25/09/2000", icon: Calendar },
                { label: "Nationality", value: "Indian", icon: Globe },
                { label: "City", value: "Mumbai", icon: MapPin },
                { label: "Postcode", value: "345020", icon: Hash },
              ].map((item, idx, arr) => (
                <div key={idx} className={`flex justify-between border-b border-app-border py-3 ${idx === arr.length - 1 ? 'last:border-0 border-none' : ''}`}>
                  <span className="text-app-textSecondary text-sm flex items-center gap-2">
                    <item.icon className="w-3.5 h-3.5" /> {item.label}
                  </span>
                  <span className="text-app-textPrimary font-medium text-sm">{item.value}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Security & Verification */}
          <div className="bg-white rounded-card shadow-card border border-app-border p-6 space-y-6">
            <div className="flex items-center gap-2 mb-2">
              <ShieldCheck className="w-5 h-5 text-brand-primary" />
              <h3 className="text-app-textPrimary font-bold text-lg">Account Security</h3>
            </div>

            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <p className="text-app-textPrimary font-semibold text-sm">2-Step Verification</p>
                  <p className="text-app-textSecondary text-xs">Secure your account with 2FA</p>
                </div>
                {user?.twoFactorAuth?.enabled ? (
                  <Badge className="bg-brand-light text-brand-primary border-none rounded-pill font-bold px-3">ENABLED</Badge>
                ) : (
                  <Badge className="bg-red-50 text-app-error border-none rounded-pill font-bold px-3">DISABLED</Badge>
                )}
              </div>

              {!user?.twoFactorAuth?.enabled && (
                <Button 
                  onClick={() => openVerification("2FA_SETUP")}
                  variant="outline" 
                  className="w-full border-brand-primary text-brand-primary hover:bg-brand-light rounded-input font-bold"
                >
                  Enable 2FA
                </Button>
              )}

              <div className="pt-4 border-t border-app-border flex items-center justify-between">
                <div className="space-y-0.5">
                  <p className="text-app-textPrimary font-semibold text-sm">Account Status</p>
                  <p className="text-app-textSecondary text-xs">Identity verification status</p>
                </div>
                {!user?.verified && (
                  <Button 
                    onClick={() => openVerification("ID_VERIFICATION")}
                    size="sm" 
                    className="bg-brand-primary hover:bg-brand-dark text-white rounded-input px-4 font-bold"
                  >
                    Verify Now
                  </Button>
                )}
              </div>

              <div className="pt-4 border-t border-app-border flex items-center justify-between">
                <div className="space-y-0.5">
                  <p className="text-app-textPrimary font-semibold text-sm">Change Password</p>
                  <p className="text-app-textSecondary text-xs">Last changed 3 months ago</p>
                </div>
                <Button variant="ghost" size="sm" className="text-brand-primary hover:bg-brand-light font-bold flex items-center gap-1">
                  <Lock className="w-3.5 h-3.5" /> Update
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;
