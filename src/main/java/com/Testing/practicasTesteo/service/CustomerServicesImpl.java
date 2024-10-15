package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.entity.Customer;
import com.Testing.practicasTesteo.entity.Wallet;
import com.Testing.practicasTesteo.exceptions.CustomerNotFoundException;
import com.Testing.practicasTesteo.respository.ArticleRepository;
import com.Testing.practicasTesteo.respository.CustomerRepository;
import com.Testing.practicasTesteo.respository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class CustomerServicesImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ArticleRepository articleRepository;


    @Override
    public List<Customer> getAllCustomers() {

        try {
            List<Customer> customerFound = customerRepository.findAll();
            if (customerFound.isEmpty()) {
                throw new CustomerNotFoundException("Customer not found");
            }
            return customerFound;
        } catch (Exception e) {
            throw new RuntimeException("Fail to get clients", e);
        }
    }

    @Override
    public Customer getCustomerById(long id) throws CustomerNotFoundException {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer saveCustomer(Customer customer) {

        Customer savedCustomer = customerRepository.save(customer);


        Wallet wallet = Wallet.builder()
                .name(savedCustomer.getName() + "'s Wallet")
                .build();

        List<Article> existingArticles = articleRepository.findAll();

        wallet.setArticles(existingArticles);

        walletRepository.save(wallet);

        return savedCustomer;
    }

    @Override
    public Customer updateCustomerById(Customer c, long id) {
        Optional<Customer> customerFound = customerRepository.findById(id);
        if (customerFound.isPresent()) {
            Customer customerUpdated = customerFound.get();
            customerUpdated.setName(c.getName());
            customerUpdated.setSurname(c.getSurname());
            customerUpdated.setAddress(c.getAddress());
            customerUpdated.setPhone(c.getPhone());
            customerUpdated.setEmail(c.getEmail());

            return customerRepository.save(customerUpdated);
        } else {

            throw new CustomerNotFoundException("Customer ID " + id + "no encontrado.");
        }

    }


    @Override
    public boolean deleteAllCustomers() {
        try {
            customerRepository.deleteAll();
            return true;

        } catch (Exception e) {
            throw new CustomerNotFoundException("Customers no encontrados.");
        }
    }

    @Override
    public boolean deleteCustomerById(long id) {
        try {
            Optional<Customer> customerFind = customerRepository.findById(id);
            return true;
        } catch (Exception e) {
            throw new CustomerNotFoundException("Customer ID " + id + " no encontrado.");
        }

    }
}