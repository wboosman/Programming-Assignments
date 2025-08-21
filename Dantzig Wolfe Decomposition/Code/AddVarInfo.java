import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddVarInfo {
    private Map<Integer, List<ArrayList<Double>>> varInfo;

    public AddVarInfo(HashMap<Integer, List<ArrayList<Double>>> newVarInfo)
    {
        this.varInfo = newVarInfo;
    }
}
