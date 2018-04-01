package ru.bravery_and_stupidity.podcast_parser.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bravery_and_stupidity.podcast_parser.model.Category;
import ru.bravery_and_stupidity.podcast_parser.repository.CategoryRepository;
import ru.bravery_and_stupidity.podcast_parser.repository.specification.SpecificationAll;

import java.util.List;

@Service
public class CategoryServiceImp implements CategoryService {

    @Autowired
    CategoryRepository repository;

    @Transactional
    @Override
    public List<Category> getList() {
        return repository.query(new SpecificationAll());
    }

    @Transactional
    @Override
    public void addList(@NotNull List<Category> categories){ repository.add(categories); }
}
