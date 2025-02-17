/*
 * Copyright 2017-2019 Baidu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#pragma once

#include "php_openrasp.h"
#include "builtin_material.h"

namespace openrasp
{
namespace data
{
class XssUserInputObject : public BuiltinMaterial
{
protected:
    //do not efree here
    zval *value = nullptr;
    const std::string name;

public:
    XssUserInputObject(const std::string &name, zval *value);
    virtual bool is_valid() const;
    virtual void fill_json_with_params(JsonReader &j) const;

    virtual OpenRASPCheckType get_builtin_check_type() const;
    virtual bool builtin_check(JsonReader &j) const;
};

} // namespace data

} // namespace openrasp