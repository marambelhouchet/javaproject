package projetjava.controller;

import outil.FileUtil;
import java.util.*;

public class ActivityController {

    public List<String> getActivities() {
        return FileUtil.read("activities.txt");
    }

    public void addActivity(String name, String cap) {
        FileUtil.save("activities.txt", name + "," + cap);
    }

    public void deleteActivity(int index) {
        List<String> list = FileUtil.read("activities.txt");
        list.remove(index);
        FileUtil.overwrite("activities.txt", list);
    }
}