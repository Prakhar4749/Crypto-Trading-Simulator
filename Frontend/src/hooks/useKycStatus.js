import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';

export function useKycStatus() {
  const { getKycStatus, user } = useAuth();
  const [kycData, setKycData] = useState(null);
  const [loading, setLoading] = useState(true);

  const refresh = async () => {
    try {
      setLoading(true);
      const data = await getKycStatus();
      setKycData(data);
    } catch (e) {
      // If can't fetch, assume not verified
      setKycData({ 
        kycStatus: 'NOT_STARTED',
        canDeposit: false 
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user) refresh();
  }, [user]);

  return { 
    kycData, 
    loading,
    canDeposit: kycData?.canDeposit === true,
    kycStatus: kycData?.kycStatus,
    missingFields: kycData?.missingFields || [],
    refresh
  };
}
