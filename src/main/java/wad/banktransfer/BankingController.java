package wad.banktransfer;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BankingController {

    @Autowired
    private AccountRepository accountRepository;

    @PostConstruct
    public void init() {
        // test data to the application
        Account account = new Account();
        account.setIban("0000");
        account.setBalance(1000);
        accountRepository.save(account);

        Account account2 = new Account();
        account2.setIban("0001");
        account2.setBalance(500);
        accountRepository.save(account2);

        Account account3 = new Account();
        account3.setIban("0002");
        account3.setBalance(50);
        accountRepository.save(account3);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("accounts", accountRepository.findAll());
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String transfer(@RequestParam String from, @RequestParam String to, @RequestParam Integer amount) {
        if (amount == null || from.equals(to)) {
            return "redirect:/transFailed";
        }

        Account accountFrom = accountRepository.findByIban(from);
        Account accountTo = accountRepository.findByIban(to);

        int accFromBalance = accountFrom.getBalance();
        int accToBalance = accountTo.getBalance();

        if (accFromBalance - amount >= 0) {
            accountFrom.setBalance(accFromBalance - amount);
            accountTo.setBalance(accToBalance + amount);
        }
        accountRepository.save(accountFrom);
        accountRepository.save(accountTo);

        return "redirect:/transCompleted";
    }

    @RequestMapping(value = "/transCompleted", method = RequestMethod.GET)
    public String transCompleted(Model model) {
        model.addAttribute("transResult","Transaction has been completed! :)");
        return list(model);
    }
    @RequestMapping(value = "/transFailed", method = RequestMethod.GET)
    public String transFailed(Model model) {
        model.addAttribute("transResult","Transaction has not been completed! :(");
        return list(model);
    }
}
