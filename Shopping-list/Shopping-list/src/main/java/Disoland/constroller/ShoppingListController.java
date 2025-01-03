package Disoland.constroller;

import Disoland.persist.ShoppingItem;
import Disoland.persist.ShoppingItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ShoppingListController {

    private final ShoppingItemRepository repository;

    @Autowired
    public ShoppingListController(ShoppingItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String indexPage(Model model) {
        model.addAttribute("items", repository.findAll());
        model.addAttribute("filterStatus", "all");
        model.addAttribute("item", new ShoppingItem());
        return "index";
    }

    @PostMapping
    public String newShoppingItem(ShoppingItem item, BindingResult result, Model model) {
        if (item.getQuantity() == null && item.getWeight() == null && item.getVolume() == null) {
            result.rejectValue("quantity", "error.item", "Необходимо указать хотя бы одно поле: количество, вес или объем");
        }

        if (result.hasErrors()) {
            model.addAttribute("item", item);
            model.addAttribute("items", repository.findAll());
            return "index";
        }

        repository.save(item);
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String deleteShoppingItem(@PathVariable("id") Long id) {
        repository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editShoppingItem(@PathVariable("id") Long id, Model model) {
        ShoppingItem item = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item ID"));
        model.addAttribute("item", item);
        return "edit";
    }

    @PostMapping("/edit/{id}")
    public String updateShoppingItem(@PathVariable("id") Long id, ShoppingItem updatedItem, BindingResult result, Model model) {
        if (updatedItem.getQuantity() == null && updatedItem.getWeight() == null && updatedItem.getVolume() == null) {
            result.rejectValue("quantity", "error.item", "Необходимо указать хотя бы одно поле: количество, вес или объем");
        }

        if (result.hasErrors()) {
            model.addAttribute("item", updatedItem);
            return "edit";
        }

        ShoppingItem item = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item ID"));
        item.setName(updatedItem.getName());
        item.setQuantity(updatedItem.getQuantity());
        item.setWeight(updatedItem.getWeight());
        item.setVolume(updatedItem.getVolume());

        repository.save(item);

        return "redirect:/";
    }

    @GetMapping("/filter/{status}")
    public String filterByStatus(@PathVariable("status") String status, Model model) {
        if (status.equalsIgnoreCase("all")) {
            model.addAttribute("items", repository.findAll());
        } else {
            boolean purchased = status.equalsIgnoreCase("purchased");
            model.addAttribute("items", repository.findByPurchased(purchased));
        }
        model.addAttribute("item", new ShoppingItem());
        model.addAttribute("filterStatus", status);
        return "index";
    }

    @PostMapping("/toggle/{id}")
    public String togglePurchasedStatus(@PathVariable("id") Long id) {
        ShoppingItem item = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item ID"));
        item.setPurchased(!item.getPurchased());
        repository.save(item);
        return "redirect:/";
    }
}
