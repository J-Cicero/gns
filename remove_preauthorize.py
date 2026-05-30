import os
import re

files_to_fix = [
    'src/main/java/com/backend/gns/student/application/controllers/DocumentEtudiantController.java',
    'src/main/java/com/backend/gns/wallet/application/controllers/WalletController.java'
]

for filepath in files_to_fix:
    if not os.path.exists(filepath):
        continue
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Remove all @PreAuthorize lines
    new_content = re.sub(r'^\s*@PreAuthorize[^\n]*\n', '', content, flags=re.MULTILINE)
    # Remove import
    new_content = re.sub(r'^import org\.springframework\.security\.access\.prepost\.PreAuthorize;\n', '', new_content, flags=re.MULTILINE)
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Removed @PreAuthorize from {filepath}")
