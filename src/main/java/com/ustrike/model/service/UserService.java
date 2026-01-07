package com.ustrike.model.service;

import com.ustrike.model.dao.ClienteDAO;
import com.ustrike.model.dao.StaffDAO;
import com.ustrike.model.dto.Cliente;
import com.ustrike.model.dto.Staff;
import com.ustrike.util.PasswordHasher;
import java.sql.SQLException;

public class UserService {
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final StaffDAO staffDAO = new StaffDAO();
    
   
    public Object authenticateUser(String email, String passwordPlain) throws SQLException {
        // 1. Cliente
        Cliente cliente = clienteDAO.selectClienteByEmail(email);
        if (cliente != null && PasswordHasher.verify(passwordPlain, cliente.getPasswordHash())) {
            return new Object[]{"cliente", cliente.getIDCliente(), cliente.getNomeCliente()};
        }
        
        // 2. Staff
        Staff staff = staffDAO.doRetrieveByEmail(email);
        if (staff != null && PasswordHasher.verify(passwordPlain, staff.getPassword())) {
            return new Object[]{"staff", staff.getIDStaff(), staff.getNomeStaff()};
        }
        
        return null; 
    }
    
    
    public boolean createUser(Cliente cliente) throws SQLException {
        // Pre-cond: email unica (ODD)
        if (clienteDAO.selectClienteByEmail(cliente.getEmail()) != null) {
            return false;
        }
        
        
        cliente.setPasswordHash(PasswordHasher.hash(cliente.getPasswordHash()));
        cliente.setPuntiTicket(0);
        
        return clienteDAO.insertCliente(cliente);
    }
    
   
    public boolean createStaff(Staff staff) throws SQLException {
        if (staffDAO.emailExists(staff.getEmail())) {
            return false;
        }
        staff.setPassword(PasswordHasher.hash(staff.getPassword()));
        return staffDAO.doSave(staff);
    }
    
    
    public boolean updateUser(Object user, String ruolo) throws SQLException {
        if ("cliente".equalsIgnoreCase(ruolo) && user instanceof Cliente) {
            return clienteDAO.updateCliente((Cliente) user);
        } else if ("staff".equalsIgnoreCase(ruolo) && user instanceof Staff) {
            return staffDAO.doUpdate((Staff) user);
        }
        return false;
    }
    
    
    public boolean changePassword(String email, String ruolo, String oldPlain, String newPlain) 
            throws SQLException {
        
        if ("cliente".equalsIgnoreCase(ruolo)) {
            Cliente c = clienteDAO.selectClienteByEmail(email);
            if (c != null && PasswordHasher.verify(oldPlain, c.getPasswordHash())) {
                return clienteDAO.updatePassword(c.getIDCliente(), PasswordHasher.hash(newPlain));
            }
        } else if ("staff".equalsIgnoreCase(ruolo)) {
            Staff s = staffDAO.doRetrieveByEmail(email);
            if (s != null && PasswordHasher.verify(oldPlain, s.getPassword())) {
                return staffDAO.updatePassword(s.getIDStaff(), PasswordHasher.hash(newPlain));
            }
        }
        return false;
    }
    
   
    public Object getUserById(int id, String ruolo) throws SQLException {
        if ("cliente".equalsIgnoreCase(ruolo)) {
            Cliente c = clienteDAO.selectClienteById(id);
            return c;
        } else if ("staff".equalsIgnoreCase(ruolo)) {
            Staff s = staffDAO.doRetrieveByKey(id);
            return s;
        }
        return null;
    }
    
    public String getUserRole(int id, String tipo) throws SQLException {
        if ("cliente".equalsIgnoreCase(tipo)) {
            return clienteDAO.selectClienteById(id) != null ? "cliente" : null;
        } else if ("staff".equalsIgnoreCase(tipo)) {
            return staffDAO.doRetrieveByKey(id) != null ? "staff" : null;
        }
        return null;
    }
}
