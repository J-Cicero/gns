import os
import re

replacements = {
    'src/main/java/com/backend/gns/admin/domain/models/Admin.java': [
        (r'package com\.backend\.gns\.user\.domain\.models;', 'package com.backend.gns.admin.domain.models;')
    ],
    'src/main/java/com/backend/gns/admin/domain/models/BankOperator.java': [
        (r'package com\.backend\.gns\.user\.domain\.models;', 'package com.backend.gns.admin.domain.models;')
    ],
    'src/main/java/com/backend/gns/student/domain/models/UniversityAdmin.java': [
        (r'package com\.backend\.gns\.user\.domain\.models;', 'package com.backend.gns.student.domain.models;')
    ]
}

for filepath, regexes in replacements.items():
    if not os.path.exists(filepath):
        print(f"Not found: {filepath}")
        continue
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    new_content = content
    for pattern, repl in regexes:
        new_content = re.sub(pattern, repl, new_content)
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Updated {filepath}")
