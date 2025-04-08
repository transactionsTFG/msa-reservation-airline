package rules;

import javax.ejb.Singleton;

import business.dto.CustomerDTO;

@Singleton
public class RulesBusinessCustomer {
    
    public boolean isValid(CustomerDTO customer) {
        return isValidName(customer.getName()) &&
               isValidEmail(customer.getEmail()) &&
               isValidPhone(customer.getPhone()) &&
               isValidDni(customer.getDni());
    }

    private boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 100;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\d{9}$");
    }

    private boolean isValidDni(String dni) {
        return dni != null && dni.matches("^\\d{8}[A-Za-z]$");
    }
}
