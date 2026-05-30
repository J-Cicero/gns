import os
import re

src_dir = 'src/main/java/com/backend/gns/parametrage'

replacements = {
    r'package com\.backend\.gns\.student\.domain\.models;': 'package com.backend.gns.parametrage.domain.models;',
    r'package com\.backend\.gns\.student\.domain\.enums;': 'package com.backend.gns.parametrage.domain.enums;',
    r'package com\.backend\.gns\.student\.domain\.services;': 'package com.backend.gns.parametrage.domain.services;',
    r'package com\.backend\.gns\.student\.domain\.services\.impl;': 'package com.backend.gns.parametrage.domain.services.impl;',
    r'package com\.backend\.gns\.student\.infrastructure\.repositories;': 'package com.backend.gns.parametrage.infrastructure.repositories;',
    r'package com\.backend\.gns\.student\.application\.controllers;': 'package com.backend.gns.parametrage.application.controllers;',
    r'package com\.backend\.gns\.student\.application\.dtos\.requests;': 'package com.backend.gns.parametrage.application.dtos.requests;',
    r'package com\.backend\.gns\.student\.application\.dtos\.responses;': 'package com.backend.gns.parametrage.application.dtos.responses;',
    r'package com\.backend\.gns\.student\.application\.mappers;': 'package com.backend.gns.parametrage.application.mappers;'
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

for root, dirs, files in os.walk(src_dir):
    for file in files:
        process_file(os.path.join(root, file))

print("Fixed packages.")
