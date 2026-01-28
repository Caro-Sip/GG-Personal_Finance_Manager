package gitgud.pfm.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import gitgud.pfm.Models.Category;

public class CategoryService {
    private final List<Category> customCategories = new ArrayList<>();
    private final AtomicInteger customIdCounter = new AtomicInteger(1000);

    public List<Category> getDefaultCategories() {
        return List.of(
            new Category(1, "Groceries", "Food and supermarket purchases", Category.Type.EXPENSE, 0.0, false),
            new Category(2, "Utilities", "Electricity, water, gas, etc.", Category.Type.EXPENSE, 0.0, false),
            new Category(3, "Salary", "Monthly salary income", Category.Type.INCOME, 0.0, false),
            new Category(4, "Transport", "Public transport, fuel, etc.", Category.Type.EXPENSE, 0.0, false),
            new Category(5, "Entertainment", "Movies, games, hobbies", Category.Type.EXPENSE, 0.0, false),
            new Category(6, "Other Income", "Other sources of income", Category.Type.INCOME, 0.0, false)
        );
    }

    public void addCustomCategory(Category category) {
        // Assign a unique id and mark as custom
        category.setId(customIdCounter.getAndIncrement());
        category.setCustom(true);
        customCategories.add(category);
    }

    public List<Category> getCustomCategories() {
        return Collections.unmodifiableList(customCategories);
    }

    public List<Category> getAllCategories() {
        List<Category> all = new ArrayList<>(getDefaultCategories());
        all.addAll(customCategories);
        return all;
    }
}
