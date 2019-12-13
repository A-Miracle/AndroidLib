package a.miracle.androidlib.widget.exp.listener;


import a.miracle.androidlib.widget.exp.models.Section;

/**
 * 节点状态变化
 */
public interface SectionStateChangeListener {
    /**
     * 节点状态变化
     * @param section Section
     * @param isOpen isExpanded
     */
    void onSectionStateChanged(Section section, boolean isOpen);
}