package Disoland.constroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import Disoland.persist.ShoppingItem;
import Disoland.persist.ShoppingItemRepository;
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
    public String indexPage(Model model){
        model.addAttribute("items", repository.findAll());
        model.addAttribute("item", new ShoppingItem());
        return "index";
    }

    @PostMapping
    public String newShoppingItem(ShoppingItem item) {
        if (item.getQuantity() == null && item.getWeight() == null && item.getVolume() == null) {
            throw new IllegalArgumentException("Необходимо указать количество, вес или объем");
        }

        repository.save(item);
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String deleteShoppingItem(@PathVariable("id") Long id){
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
    public String updateShoppingItem(@PathVariable("id") Long id, ShoppingItem updatedItem) {
        ShoppingItem item = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item ID"));

        item.setName(updatedItem.getName());
        item.setQuantity(updatedItem.getQuantity());
        item.setWeight(updatedItem.getWeight());
        item.setVolume(updatedItem.getVolume());

        repository.save(item);

        return "redirect:/";
    }
}
