package redis.resp.commands.library;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespArray;
import redis.resp.types.RespSortedMap;

public class RespCommandLibrary {

    private final TreeMap<String, RespLibraryFunction> functions;
    public static final RespCommandLibrary INSTANCE = new RespCommandLibrary();

    public RespCommandLibrary() {
        this.functions = new TreeMap<>();
        init();
    }

    public void register(RespLibraryFunction function) {
        this.functions.put(function.getName().toLowerCase(), function);
    }

    private void init() {
        this.register(new CmdPing(INSTANCE));
        this.register(new CmdCommand(INSTANCE));
        this.register(new CmdEcho(INSTANCE));
        this.register(new CmdSet(INSTANCE));
        this.register(new CmdGet(INSTANCE));
    }

    public RespLibraryFunction get(String function) throws RespCommandException {
        var f = functions.get(function.toLowerCase());
        if (f == null) {
            throw new RespCommandException("Function '" + function + "' does not exists");
        }
        return f;
    }

    public RespResponse execute(RespRequest request) throws RespCommandException {
        var f = get(request.getFunction());
        var subfunction = request.getSubFunction();
        if (subfunction.isPresent() && f.hasSubFunction(subfunction.get())) {
            return f.execute(request, subfunction.get().toLowerCase());
        } else {
            return f.execute(request);
        }
    }

    public RespSortedMap getCommandDocs(Optional<RespArray> filter) {
        var map = new RespSortedMap();
        for (Map.Entry<String, RespLibraryFunction> entry : functions.entrySet()) {
            var name = entry.getKey();
            var function = entry.getValue();
            if (filter.isEmpty() || filter.get().contains(function.getName().toLowerCase())) {
                map.put(name.toLowerCase(), function.getCommandDocs());
            }
        }
        return map;
    }

}
