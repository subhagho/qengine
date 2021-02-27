/*
 *  Copyright (2019) Subhabrata Ghosh (subho dot ghosh at outlook dot com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.codekutter.qengine.common;

public enum EObjectState implements IState<EObjectState> {
    Unknown, Initialized, Available, Disposed, Error;

    /**
     * Get the state that represents an error state.
     *
     * @return - Error state.
     */
    @Override
    public EObjectState getErrorState() {
        return Error;
    }
}
