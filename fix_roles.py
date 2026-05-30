import os
import re

src_dir = 'src/main/java/com/backend/gns'

replacements = {
    r'com\.backend\.gns\.user\.infrastructure\.repositories\.BankOperatorRepository': 'com.backend.gns.admin.infrastructure.repositories.BankOperatorRepository',
    r'package com\.backend\.gns\.user\.infrastructure\.repositories;': 'package com.backend.gns.admin.infrastructure.repositories;',

    r'com\.backend\.gns\.admin\.infrastructure\.repositories\.UniversityAdminRepository': 'com.backend.gns.student.infrastructure.repositories.UniversityAdminRepository',
    r'package com\.backend\.gns\.admin\.infrastructure\.repositories;\s*(?=.*UniversityAdminRepository)': 'package com.backend.gns.student.infrastructure.repositories;',
    r'package com\.backend\.gns\.admin\.infrastructure\.repositories;': 'package com.backend.gns.admin.infrastructure.repositories;', # Leave normal admin repo alone

    r'com\.backend\.gns\.admin\.domain\.services\.UniversityAdminService': 'com.backend.gns.student.domain.services.UniversityAdminService',
    r'package com\.backend\.gns\.admin\.domain\.services;\s*(?=.*UniversityAdminService)': 'package com.backend.gns.student.domain.services;',
    
    r'com\.backend\.gns\.admin\.domain\.services\.impl\.UniversityAdminServiceImpl': 'com.backend.gns.student.domain.services.impl.UniversityAdminServiceImpl',
    r'package com\.backend\.gns\.admin\.domain\.services\.impl;\s*(?=.*UniversityAdminServiceImpl)': 'package com.backend.gns.student.domain.services.impl;',

    r'com\.backend\.gns\.admin\.application\.controllers\.UniversityAdminController': 'com.backend.gns.student.application.controllers.UniversityAdminController',
    r'package com\.backend\.gns\.admin\.application\.controllers;\s*(?=.*UniversityAdminController)': 'package com.backend.gns.student.application.controllers;',

    r'com\.backend\.gns\.admin\.application\.mappers\.UniversityAdminMapper': 'com.backend.gns.student.application.mappers.UniversityAdminMapper',
    r'package com\.backend\.gns\.admin\.application\.mappers;\s*(?=.*UniversityAdminMapper)': 'package com.backend.gns.student.application.mappers;',

    r'com\.backend\.gns\.admin\.application\.dtos\.requests\.UniversityAdminRequest': 'com.backend.gns.student.application.dtos.requests.UniversityAdminRequest',
    r'package com\.backend\.gns\.admin\.application\.dtos\.requests;\s*(?=.*UniversityAdminRequest)': 'package com.backend.gns.student.application.dtos.requests;',

    r'com\.backend\.gns\.admin\.application\.dtos\.responses\.UniversityAdminResponse': 'com.backend.gns.student.application.dtos.responses.UniversityAdminResponse',
    r'package com\.backend\.gns\.admin\.application\.dtos\.responses;\s*(?=.*UniversityAdminResponse)': 'package com.backend.gns.student.application.dtos.responses;'
}

def process_file(filepath):
    if not filepath.endswith('.java'):
        return
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    new_content = content

    # Fix package declarations for UniversityAdmin files that were moved to student
    if 'UniversityAdmin' in filepath and 'student' in filepath:
        new_content = new_content.replace('package com.backend.gns.admin.', 'package com.backend.gns.student.')
    # Fix package declarations for BankOperator files that were moved to admin
    elif 'BankOperatorRepository' in filepath and 'admin' in filepath:
        new_content = new_content.replace('package com.backend.gns.user.', 'package com.backend.gns.admin.')

    for pattern, repl in replacements.items():
        new_content = re.sub(pattern, repl, new_content)
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Updated {filepath}")

for root, dirs, files in os.walk('src'):
    for file in files:
        process_file(os.path.join(root, file))

print("Packages and imports updated.")
