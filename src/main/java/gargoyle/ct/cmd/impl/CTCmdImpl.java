package gargoyle.ct.cmd.impl;

import gargoyle.ct.cmd.CTCmd;
import gargoyle.ct.cmd.args.impl.CTArgsImpl;
import gargoyle.ct.log.Log;
import gargoyle.ct.util.CTTimeUtil;

public class CTCmdImpl extends CTArgsImpl implements CTCmd {
    private static final String MSG_FAKE_TIME_INVALID = "fake time not set";

    public CTCmdImpl(String[] args) {
        super(args);
    }

    @Override
    public long getFakeTime() {
        try {
            return has(0) ? CTTimeUtil.parseHHMMSS(getString(0)) : 0;
        } catch (NumberFormatException ex) {
            Log.info(MSG_FAKE_TIME_INVALID);
            return 0;
        }
    }

    @Override
    public boolean isDebug() {
        return size() == 1 || size() > 1 && getBoolean(1);
    }
}
