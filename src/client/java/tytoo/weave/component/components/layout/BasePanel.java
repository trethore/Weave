package tytoo.weave.component.components.layout;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.constraints.Constraints;

public class BasePanel<T extends BasePanel<T>> extends Component<T> {
    protected BasePanel() {
        this.setWidth(Constraints.childBased());
        this.setHeight(Constraints.childBased());
    }
}