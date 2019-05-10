/*
  Copyright © 2019 Pasqual K. | All rights reserved
 */

package systems.reformcloud.libloader.libraries;

import systems.reformcloud.libloader.utility.Dependency;

import java.io.Serializable;

/**
 * @author _Klaro | Pasqual K. / created on 22.01.2019
 */

public final class JLine extends Dependency implements Serializable {
    private static final long serialVersionUID = -5341268101711897840L;

    public JLine() {
        super(null);
    }

    @Override
    public String getGroupID() {
        return "jline";
    }

    @Override
    public String getName() {
        return "jline";
    }

    @Override
    public String getVersion() {
        return "2.14.6";
    }
}