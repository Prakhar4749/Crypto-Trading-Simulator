import { useNavigate } from 'react-router-dom';

export default function KycDepositBlock({ kycData, children }) {
  const navigate = useNavigate();

  // If verified, show children normally
  if (kycData?.canDeposit) {
    return children;
  }

  const missing = kycData?.missingFields || [];

  return (
    <div>
      {/* Greyed out children */}
      <div className="opacity-40 pointer-events-none select-none cursor-not-allowed">
        {children}
      </div>

      {/* Warning block */}
      <div className="mt-3 bg-amber-500/10 border border-amber-500/30 rounded-xl p-4">
        <p className="text-amber-400 font-semibold text-sm mb-1">
          ⚠️ Verification Required
        </p>
        <p className="text-gray-400 text-xs mb-3">
          Complete your profile to deposit real funds.
        </p>

        {missing.length > 0 && (
          <div className="mb-3">
            <p className="text-gray-500 text-xs mb-1">Missing:</p>
            <ul className="space-y-1">
              {missing.map((item, i) => (
                <li key={i} className="text-gray-400 text-xs flex items-center gap-1">
                  <span className="text-red-400">
                    ○
                  </span>
                  {item}
                </li>
              ))}
            </ul>
          </div>
        )}

        <button
          onClick={() => navigate('/profile', { state: { openEdit: true } })}
          className="w-full bg-amber-500/20 border border-amber-500/50 text-amber-400 py-2 rounded-lg text-sm font-medium hover:bg-amber-500/30 transition-colors"
        >
          Complete Profile →
        </button>
      </div>
    </div>
  );
}
