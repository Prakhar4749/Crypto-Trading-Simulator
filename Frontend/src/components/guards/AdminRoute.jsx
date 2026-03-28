import { Navigate } from 'react-router-dom';

const AdminRoute = ({ children }) => {
  const role = localStorage.getItem('userRole');
  
  if (role !== 'ROLE_ADMIN') {
    console.warn("[AdminRoute] Access denied. Redirecting to home.");
    return <Navigate to="/" replace />;
  }
  
  return children;
};

export default AdminRoute;
