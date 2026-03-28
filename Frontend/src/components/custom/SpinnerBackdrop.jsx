/* eslint-disable react/prop-types */
const SpinnerBackdrop = ({ show, message }) => {
  if (!show) return null;
  
  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex flex-col items-center justify-center z-[9999] transition-all duration-300">
      <div className="bg-[#1a1a2e] p-8 rounded-2xl shadow-2xl border border-white/10 flex flex-col items-center space-y-6 max-w-xs w-full mx-4">
        <div className="relative">
          <div className="w-16 h-16 border-4 border-brand-primary/20 border-t-brand-primary rounded-full animate-spin"></div>
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="w-8 h-8 bg-brand-primary/10 rounded-full animate-pulse"></div>
          </div>
        </div>
        
        {message && (
          <div className="text-center space-y-2">
            <p className="text-white font-bold text-lg tracking-tight">{message}</p>
            <p className="text-gray-400 text-xs animate-pulse">Please do not refresh the page</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default SpinnerBackdrop;
