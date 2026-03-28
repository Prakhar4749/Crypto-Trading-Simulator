import { useForm } from 'react-hook-form';
import { useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import showToast from '../../utils/toast';

export default function EditProfileModal({ isOpen, onClose }) {
  const { user, updateUserProfile } = useAuth();
  const [loading, setLoading] = useState(false);
  
  const { register, handleSubmit, formState: { errors } } = useForm({
    defaultValues: {
      phoneNumber: user?.phoneNumber || '',
      address: user?.address || '',
      city: user?.city || '',
      state: user?.state || '',
      country: user?.country || '',
      pinCode: user?.pinCode || '',
      profilePicture: user?.profilePicture || '',
    }
  });

  const onSubmit = async (data) => {
    // Remove empty fields
    const cleanData = Object.fromEntries(
      Object.entries(data).filter(([_, v]) => v !== '')
    );
    
    setLoading(true);
    try {
      await updateUserProfile(cleanData);
      // updateUserProfile already shows success toast
      onClose();
    } catch (error) {
      // updateUserProfile already shows error toast via showToast.fromError
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-[100] bg-black/70 flex items-center justify-center p-4"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}>
      
      <div className="bg-[#1a1a2e] rounded-2xl w-full max-w-lg border border-white/10 max-h-[90vh] overflow-y-auto">
        
        {/* Modal Header */}
        <div className="flex items-center justify-between p-6 border-b border-white/10">
          <h2 className="text-white text-xl font-bold">
            Edit Profile
          </h2>
          <button 
            onClick={onClose}
            className="text-gray-400 hover:text-white text-2xl leading-none"
          >
            ×
          </button>
        </div>

        {/* Form */}
        <form 
          onSubmit={handleSubmit(onSubmit)}
          className="p-6 space-y-4"
        >
          
          {/* Phone */}
          <div>
            <label className="text-gray-400 text-sm mb-1 block">
              Phone Number
            </label>
            <input
              {...register('phoneNumber', {
                pattern: {
                  value: /^[6-9][0-9]{9}$/,
                  message: 'Enter valid 10-digit mobile number'
                }
              })}
              type="tel"
              placeholder="9876543210"
              className="w-full bg-[#0f0f1a] border border-white/10 rounded-xl px-4 py-3 text-white placeholder-gray-600 focus:border-[#00B386] focus:outline-none transition-colors"
            />
            {errors.phoneNumber && (
              <p className="text-red-400 text-xs mt-1">
                {errors.phoneNumber.message}
              </p>
            )}
          </div>

          {/* Address */}
          <div>
            <label className="text-gray-400 text-sm mb-1 block">
              Address
            </label>
            <textarea
              {...register('address')}
              rows={2}
              placeholder="Street address"
              className="w-full bg-[#0f0f1a] border border-white/10 rounded-xl px-4 py-3 text-white placeholder-gray-600 focus:border-[#00B386] focus:outline-none transition-colors resize-none"
            />
          </div>

          {/* City + State grid */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-gray-400 text-sm mb-1 block">City</label>
              <input
                {...register('city')}
                placeholder="Mumbai"
                className="w-full bg-[#0f0f1a] border border-white/10 rounded-xl px-4 py-3 text-white placeholder-gray-600 focus:border-[#00B386] focus:outline-none transition-colors"
              />
            </div>
            <div>
              <label className="text-gray-400 text-sm mb-1 block">State</label>
              <input
                {...register('state')}
                placeholder="Maharashtra"
                className="w-full bg-[#0f0f1a] border border-white/10 rounded-xl px-4 py-3 text-white placeholder-gray-600 focus:border-[#00B386] focus:outline-none transition-colors"
              />
            </div>
          </div>

          {/* Country + Pin grid */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-gray-400 text-sm mb-1 block">
                Country
              </label>
              <input
                {...register('country')}
                placeholder="India"
                className="w-full bg-[#0f0f1a] border border-white/10 rounded-xl px-4 py-3 text-white placeholder-gray-600 focus:border-[#00B386] focus:outline-none transition-colors"
              />
            </div>
            <div>
              <label className="text-gray-400 text-sm mb-1 block">
                PIN Code
              </label>
              <input
                {...register('pinCode', {
                  pattern: {
                    value: /^[0-9]{6}$/,
                    message: '6 digit PIN'
                  }
                })}
                placeholder="400001"
                className="w-full bg-[#0f0f1a] border border-white/10 rounded-xl px-4 py-3 text-white placeholder-gray-600 focus:border-[#00B386] focus:outline-none transition-colors"
              />
              {errors.pinCode && (
                <p className="text-red-400 text-xs mt-1">
                  {errors.pinCode.message}
                </p>
              )}
            </div>
          </div>

          {/* Submit */}
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-[#00B386] text-white py-3 rounded-xl font-semibold hover:bg-[#009970] disabled:opacity-50 disabled:cursor-not-allowed transition-colors mt-2"
          >
            {loading ? 'Saving...' : 'Save Changes'}
          </button>
        </form>
      </div>
    </div>
  );
}
