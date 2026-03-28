import { useEffect, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function KycStatusCard({ onAction }) {
  const { getKycStatus, user } = useAuth();
  const navigate = useNavigate();
  const [kycData, setKycData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadKycStatus();
  }, [user]); // Refresh when user object changes (e.g. after profile update)

  const loadKycStatus = async () => {
    try {
      const data = await getKycStatus();
      setKycData(data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  if (loading || !kycData) return null;
  if (kycData.kycStatus === 'VERIFIED') return null;

  const steps = [
    {
      label: 'Email Verified',
      done: kycData.isEmailVerified,
      action: () => navigate('/profile/verify'),
      actionLabel: 'Verify Email'
    },
    {
      label: 'Phone Number Added',
      done: kycData.hasPhone,
      action: () => onAction?.('phone'),
      actionLabel: 'Add Phone'
    },
    {
      label: 'Address Filled',
      done: kycData.hasAddress,
      action: () => onAction?.('address'),
      actionLabel: 'Add Address'
    }
  ];

  const completedCount = steps.filter(s => s.done).length;

  return (
    <div id="kyc-section" className="bg-amber-500/10 border border-amber-500/30 rounded-xl p-4 mb-6">
      <div className="flex items-center justify-between mb-3">
        <div>
          <h3 className="text-amber-400 font-semibold text-sm">
            ⚠️ Complete Verification
          </h3>
          <p className="text-gray-400 text-xs mt-0.5">
            Required to deposit real funds
          </p>
        </div>
        <span className="text-amber-400 text-sm font-bold">
          {completedCount}/3
        </span>
      </div>

      {/* Progress bar */}
      <div className="h-1.5 bg-white/10 rounded-full mb-3">
        <div 
          className="h-full bg-amber-400 rounded-full transition-all duration-500"
          style={{ width: `${(completedCount/3) * 100}%` }}
        />
      </div>

      {/* Steps checklist */}
      <div className="space-y-1.5">
        {steps.map((step, i) => (
          <div key={i} className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className={step.done ? 'text-green-400' : 'text-gray-500'}>
                {step.done ? '✅' : '○'}
              </span>
              <span className={`text-xs ${step.done ? 'text-gray-400 line-through' : 'text-gray-300'}`}>
                {step.label}
              </span>
            </div>
            {!step.done && (
              <button
                onClick={step.action}
                className="text-amber-400 text-xs hover:underline"
              >
                {step.actionLabel} →
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
