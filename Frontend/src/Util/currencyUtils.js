const EXCHANGE_RATE = Number(import.meta.env.VITE_EXCHANGE_RATE) || 10000;

export const formatUSD = (amount) => {
  if (amount === undefined || amount === null) return "$0.00";
  return `$${Number(amount).toLocaleString('en-US', { 
    minimumFractionDigits: 2, 
    maximumFractionDigits: 2 
  })}`;
};

export const usdToInr = (usdAmount) => {
  if (!usdAmount) return 0;
  return usdAmount / EXCHANGE_RATE;
};

export const inrToUsd = (inrAmount) => {
  if (!inrAmount) return 0;
  return inrAmount * EXCHANGE_RATE;
};

export const formatInr = (amount) => {
  if (amount === undefined || amount === null) return "₹0.00";
  return `₹${Number(amount).toLocaleString('en-IN', { 
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })}`;
};
