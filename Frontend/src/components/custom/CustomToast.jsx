// Toast.js
import React, { useEffect, useState } from 'react';

const CustomToast = ({ message, show, onClose }) => {
    const [showToast, setShowToast] = useState(false);

    const handleCloseToast = () => {
        setShowToast(false);
      };

      
  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        handleCloseToast();
      }, 2000); // Auto close after 1 second

      return () => clearTimeout(timer); // Cleanup timer on component unmount
    }
  }, [showToast, onClose]);

  useEffect(()=>{
    setShowToast(show)
  },[show])

  if (!showToast) return null;

  return (
    <div className="fixed top-4 right-4 bg-app-error text-white px-6 py-3 rounded-input shadow-card font-medium z-[100] animate-in slide-in-from-right duration-300">
      {message}
    </div>
  );
};

export default CustomToast;
