import os
import re
import shutil

src_dir = 'src/main/java/com/backend/gns'

# 1. Move academique -> student
def move_dir(src, dst):
    if not os.path.exists(src):
        return
    if not os.path.exists(dst):
        os.makedirs(dst)
    for item in os.listdir(src):
        s = os.path.join(src, item)
        d = os.path.join(dst, item)
        if os.path.isdir(s):
            move_dir(s, d)
        else:
            if os.path.exists(d):
                os.remove(d)
            shutil.move(s, d)

# academique -> student
move_dir(os.path.join(src_dir, 'academique'), os.path.join(src_dir, 'student'))
if os.path.exists(os.path.join(src_dir, 'academique')):
    shutil.rmtree(os.path.join(src_dir, 'academique'))

# 2. parametrage -> student (ParametreDbs) and core (ParametreGns)
parametrage_dir = os.path.join(src_dir, 'parametrage')
if os.path.exists(parametrage_dir):
    for root, dirs, files in os.walk(parametrage_dir):
        for file in files:
            if file.endswith('.java'):
                src_file = os.path.join(root, file)
                rel_path = os.path.relpath(src_file, parametrage_dir)
                if 'Dbs' in file or 'Dbs' in rel_path:
                    # Move to student
                    dst_file = os.path.join(src_dir, 'student', rel_path)
                    os.makedirs(os.path.dirname(dst_file), exist_ok=True)
                    shutil.move(src_file, dst_file)
                elif 'Gns' in file or 'Gns' in rel_path:
                    # Move to core/config or core/domain
                    # Let's just put it in core keeping the application/domain/infrastructure structure for now
                    # Wait, core doesn't have application/domain/infrastructure at the root, it has core/config, core/domain/enums etc.
                    # It's better to move ParametreGns to 'core/parametrage/'
                    dst_file = os.path.join(src_dir, 'core', 'parametrage', rel_path)
                    os.makedirs(os.path.dirname(dst_file), exist_ok=True)
                    shutil.move(src_file, dst_file)
    shutil.rmtree(parametrage_dir)

# 3. user models -> admin and student
user_models_dir = os.path.join(src_dir, 'user', 'domain', 'models')
if os.path.exists(user_models_dir):
    admin_model = os.path.join(user_models_dir, 'Admin.java')
    bank_model = os.path.join(user_models_dir, 'BankOperator.java')
    uni_model = os.path.join(user_models_dir, 'UniversityAdmin.java')
    
    if os.path.exists(admin_model):
        os.makedirs(os.path.join(src_dir, 'admin', 'domain', 'models'), exist_ok=True)
        shutil.move(admin_model, os.path.join(src_dir, 'admin', 'domain', 'models', 'Admin.java'))
        
    if os.path.exists(bank_model):
        os.makedirs(os.path.join(src_dir, 'admin', 'domain', 'models'), exist_ok=True)
        shutil.move(bank_model, os.path.join(src_dir, 'admin', 'domain', 'models', 'BankOperator.java'))
        
    if os.path.exists(uni_model):
        os.makedirs(os.path.join(src_dir, 'student', 'domain', 'models'), exist_ok=True)
        shutil.move(uni_model, os.path.join(src_dir, 'student', 'domain', 'models', 'UniversityAdmin.java'))

# Replacements
replacements = {
    r'com\.backend\.gns\.academique': 'com.backend.gns.student',
    r'com\.backend\.gns\.parametrage\.([a-z\.]+)\.([A-Za-z]+Dbs[A-Za-z]*)': r'com.backend.gns.student.\1.\2',
    r'com\.backend\.gns\.parametrage\.([a-z\.]+)\.([A-Za-z]+Gns[A-Za-z]*)': r'com.backend.gns.core.parametrage.\1.\2',
    r'com\.backend\.gns\.parametrage': 'com.backend.gns.core.parametrage', # Catch remaining ParametreGns packages
    r'com\.backend\.gns\.user\.domain\.models\.Admin': 'com.backend.gns.admin.domain.models.Admin',
    r'com\.backend\.gns\.user\.domain\.models\.BankOperator': 'com.backend.gns.admin.domain.models.BankOperator',
    r'com\.backend\.gns\.user\.domain\.models\.UniversityAdmin': 'com.backend.gns.student.domain.models.UniversityAdmin',
}

def process_file(filepath):
    if not filepath.endswith('.java'):
        return
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    new_content = content
    for pattern, repl in replacements.items():
        new_content = re.sub(pattern, repl, new_content)
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Updated {filepath}")

for root, dirs, files in os.walk('src'):
    for file in files:
        process_file(os.path.join(root, file))

print("Phase 1, 2, 3 complete.")
