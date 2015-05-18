/*
 * Created by Hanks
 * Copyright (c) 2015 NaShangBan. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.hanks.com.conquer.otto;

import com.squareup.otto.Bus;

/**
 * Created by Administrator on 2015/5/15.
 */
public final class BusProvider {
    private static final Bus bus = new Bus();
    public static Bus getInstance(){
        return bus;
    }
    private BusProvider(){}
}
