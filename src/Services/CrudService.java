package Services;

import java.util.List;
import java.util.Map;

public class CrudService<Entity> implements CrudServiceInterface<Entity> {
    @Override
    public void create(Map<String, ?> dataToFill) {

    }

    @Override
    public Entity read(Long id) {
        return null;
    }

    @Override
    public List<Entity> getAll() {
        return null;
    }

    @Override
    public void update(Long id, Map<String, ?> dataToFill) {

    }

    @Override
    public void delete(Long id) {

    }
}
