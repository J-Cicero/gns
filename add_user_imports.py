import os

files_to_fix = [
    'src/main/java/com/backend/gns/admin/domain/models/Admin.java',
    'src/main/java/com/backend/gns/admin/domain/models/BankOperator.java',
    'src/main/java/com/backend/gns/student/domain/models/UniversityAdmin.java'
]

import_statement = "import com.backend.gns.user.domain.models.User;\n"

for filepath in files_to_fix:
    if not os.path.exists(filepath):
        print(f"Not found: {filepath}")
        continue
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    if import_statement not in content:
        # Insert after the package line
        lines = content.split('\n')
        for i, line in enumerate(lines):
            if line.startswith('package '):
                lines.insert(i + 1, '\n' + import_statement.strip())
                break
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write('\n'.join(lines))
        print(f"Added User import to {filepath}")
