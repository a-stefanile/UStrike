package com.ustrike.model.service;

import com.ustrike.model.dao.ClienteDAO;
import com.ustrike.model.dao.StaffDAO;
import com.ustrike.model.dto.Cliente;
import com.ustrike.model.dto.Staff;
import com.ustrike.util.PasswordHasher;
import java.sql.SQLException;

public class UserService {
    private  ClienteDAO clienteDAO;
    private  StaffDAO staffDAO;
    
    public UserService() {
    	this.clienteDAO = new ClienteDAO();
    	this.staffDAO = new StaffDAO();
    }
    
    public UserService(ClienteDAO clienteDAO, StaffDAO staffDAO) {
    	this.clienteDAO = clienteDAO;
    	this.staffDAO = staffDAO;
    }
    
    // ðŸ”� LOGIN UNIFICATO (PBKDF2 salt:hash)
    public Object authenticateUser(String email, String passwordPlain) throws SQLException {
        // 1. CLIENTE UC2
        Cliente cliente = clienteDAO.selectClienteByEmail(email);
        if (cliente != null && PasswordHasher.verify(passwordPlain, cliente.getPasswordHash())) {
            return new Object[]{"cliente", cliente.getIDCliente(), cliente.getNomeCliente()};
        }
        
        // 2. STAFF UC21
        Staff staff = staffDAO.doRetrieveByEmail(email);
        if (staff != null && PasswordHasher.verify(passwordPlain, staff.getPasswordHash())) {
            return new Object[]{"staff", staff.getIDStaff(), staff.getNomeStaff()};
        }
        
        return null;  // Login fallito
    }
    
    // âž• REGISTRAZIONE CLIENTE UC1
    public boolean createCliente(Cliente cliente, String passwordPlain) throws SQLException {
        // Email unica ODD
        if (clienteDAO.selectClienteByEmail(cliente.getEmail()) != null) {
            return false;
        }
        
        cliente.setPasswordHash(PasswordHasher.hash(passwordPlain));  // âœ… PBKDF2 salt:hash
        cliente.setPuntiTicket(0);
        return clienteDAO.insertCliente(cliente);
    }
    
    // âž• STAFF (admin)
    public boolean createStaff(Staff staff, String passwordPlain) throws SQLException {
        if (staffDAO.emailExists(staff.getEmail())) {
            return false;
        }
        staff.setPasswordHash(PasswordHasher.hash(passwordPlain));  // âœ… Uniforme
        return staffDAO.doSave(staff);
    }
    
    // âœ�ï¸� UPDATE PROFILO
    public boolean updateUser(Object user, String ruolo) throws SQLException {
        if ("cliente".equalsIgnoreCase(ruolo) && user instanceof Cliente) {
            Cliente c = (Cliente) user;
            return clienteDAO.updateCliente(c);
        } else if ("staff".equalsIgnoreCase(ruolo) && user instanceof Staff) {
            return staffDAO.doUpdate((Staff) user);
        }
        return false;
    }
    
    // ðŸ”‘ CHANGE PASSWORD (verifica old)
    public boolean changePassword(String email, String ruolo, String oldPlain, String newPlain) 
            throws SQLException {
        
        if ("cliente".equalsIgnoreCase(ruolo)) {
            Cliente c = clienteDAO.selectClienteByEmail(email);
            if (c != null && PasswordHasher.verify(oldPlain, c.getPasswordHash())) {
                return clienteDAO.updatePassword(c.getIDCliente(), PasswordHasher.hash(newPlain));
            }
        } else if ("staff".equalsIgnoreCase(ruolo)) {
            Staff s = staffDAO.doRetrieveByEmail(email);
            if (s != null && PasswordHasher.verify(oldPlain, s.getPasswordHash())) {
                return staffDAO.updatePassword(s.getIDStaff(), PasswordHasher.hash(newPlain));
            }
        }
        return false;
    }
    
    // ðŸ‘¤ GET BY ID
    public Object getUserById(int id, String ruolo) throws SQLException {
        if ("cliente".equalsIgnoreCase(ruolo)) {
            return clienteDAO.selectClienteById(id);
        } else if ("staff".equalsIgnoreCase(ruolo)) {
            return staffDAO.doRetrieveByKey(id);
        }
        return null;
    }
}
