import os
import re

src_dir = 'src'

replacements = {
    # Fix the enums that were just moved to core
    r'com\.backend\.gns\.Shared\.domain\.enums': 'com.backend.gns.core.domain.enums',

    # Fix the remaining generic Shared application/domain/infrastructure that belonged to the refactored modules but whose packages were not correctly updated
    r'package com\.backend\.gns\.Shared\.application\.mappers;': 'package com.backend.gns.parametrage.application.mappers;',
    r'package com\.backend\.gns\.Shared\.application\.dtos\.requests;': 'package com.backend.gns.parametrage.application.dtos.requests;',
    r'package com\.backend\.gns\.Shared\.application\.dtos\.responses;': 'package com.backend.gns.parametrage.application.dtos.responses;',
    r'package com\.backend\.gns\.Shared\.application\.controllers;': 'package com.backend.gns.parametrage.application.controllers;',
    r'package com\.backend\.gns\.Shared\.infrastructure\.repositories;': 'package com.backend.gns.parametrage.infrastructure.repositories;',
    r'package com\.backend\.gns\.Shared\.domain\.services\.impl;': 'package com.backend.gns.parametrage.domain.services.impl;',
    r'package com\.backend\.gns\.Shared\.domain\.services;': 'package com.backend.gns.parametrage.domain.services;',
    r'package com\.backend\.gns\.Shared\.domain\.models;': 'package com.backend.gns.parametrage.domain.models;',
    r'package com\.backend\.gns\.Shared\.domain\.enums;': 'package com.backend.gns.parametrage.domain.enums;',

    # Fix Universite package references that were missed
    r'package com\.backend\.gns\.Shared\.application\.mappers;\s*(?=import.*Universite)': 'package com.backend.gns.academique.application.mappers;',
    r'package com\.backend\.gns\.Shared\.application\.dtos\.requests;\s*(?=import.*UniversiteRequest)': 'package com.backend.gns.academique.application.dtos.requests;',
    r'package com\.backend\.gns\.Shared\.application\.dtos\.responses;\s*(?=import.*UniversiteResponse)': 'package com.backend.gns.academique.application.dtos.responses;',
    r'package com\.backend\.gns\.Shared\.application\.controllers;\s*(?=import.*UniversiteController)': 'package com.backend.gns.academique.application.controllers;',
    r'package com\.backend\.gns\.Shared\.domain\.models;\s*(?=import.*Universite)': 'package com.backend.gns.academique.domain.models;',
    r'package com\.backend\.gns\.Shared\.domain\.services;\s*(?=import.*UniversiteService)': 'package com.backend.gns.academique.domain.services;',
    r'package com\.backend\.gns\.Shared\.domain\.services\.impl;\s*(?=import.*UniversiteServiceImpl)': 'package com.backend.gns.academique.domain.services.impl;',
    r'package com\.backend\.gns\.Shared\.infrastructure\.repositories;\s*(?=import.*UniversiteRepository)': 'package com.backend.gns.academique.infrastructure.repositories;',

    # Broad catch for remaining Shared imports
    r'com\.backend\.gns\.Shared\.': 'com.backend.gns.core.',
}

def process_file(filepath):
    if not filepath.endswith('.java'):
        return
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    new_content = content
    
    # Custom fix for Universite files
    if 'Universite' in filepath and 'academique' in filepath:
        new_content = new_content.replace('package com.backend.gns.parametrage.', 'package com.backend.gns.academique.')
        new_content = new_content.replace('package com.backend.gns.Shared.', 'package com.backend.gns.academique.')
    elif 'ParametreGns' in filepath and 'parametrage' in filepath:
        new_content = new_content.replace('package com.backend.gns.Shared.', 'package com.backend.gns.parametrage.')

    for pattern, repl in replacements.items():
        new_content = re.sub(pattern, repl, new_content)
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Updated {filepath}")

for root, dirs, files in os.walk(src_dir):
    for file in files:
        process_file(os.path.join(root, file))

print("Refactoring 2 complete.")
