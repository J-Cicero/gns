import os
import re

def fix_packages(directory, base_package):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if not file.endswith('.java'): continue
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # The expected package based on the file path
            # filepath: src/main/java/com/backend/gns/student/domain/models/ParametreDbs.java
            # rel_path: com/backend/gns/student/domain/models/ParametreDbs.java
            rel_path = os.path.relpath(root, 'src/main/java')
            expected_pkg = rel_path.replace(os.sep, '.')
            
            # Replace package declaration
            new_content = re.sub(r'package\s+com\.backend\.gns[a-zA-Z0-9\._]*;', f'package {expected_pkg};', content)
            
            if new_content != content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"Fixed package in {filepath}")

fix_packages('src/main/java/com/backend/gns/student', 'com.backend.gns.student')
fix_packages('src/main/java/com/backend/gns/core/parametrage', 'com.backend.gns.core.parametrage')
