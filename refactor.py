import os
import re

src_dir = 'src'

replacements = {
    r'com\.backend\.gns\.Shared\.exception': 'com.backend.gns.core.exception',
    r'com\.backend\.gns\.Shared\.security': 'com.backend.gns.core.security',
    r'com\.backend\.gns\.Shared\.config': 'com.backend.gns.core.config',
    r'com\.backend\.gns\.Shared\.utils': 'com.backend.gns.core.utils',
    r'com\.backend\.gns\.Shared\.ai': 'com.backend.gns.core.ai',
    r'com\.backend\.gns\.Shared\.email': 'com.backend.gns.core.email',
    r'com\.backend\.gns\.Shared\.storage': 'com.backend.gns.core.storage',
    
    r'com\.backend\.gns\.Shared\.wallet': 'com.backend.gns.wallet',
    r'com\.backend\.gns\.Shared\.user': 'com.backend.gns.user',
    
    # Universite
    r'com\.backend\.gns\.Shared\.domain\.models\.Universite': 'com.backend.gns.academique.domain.models.Universite',
    r'com\.backend\.gns\.Shared\.domain\.services\.UniversiteService': 'com.backend.gns.academique.domain.services.UniversiteService',
    r'com\.backend\.gns\.Shared\.domain\.services\.impl\.UniversiteServiceImpl': 'com.backend.gns.academique.domain.services.impl.UniversiteServiceImpl',
    r'com\.backend\.gns\.Shared\.infrastructure\.repositories\.UniversiteRepository': 'com.backend.gns.academique.infrastructure.repositories.UniversiteRepository',
    r'com\.backend\.gns\.Shared\.application\.controllers\.UniversiteController': 'com.backend.gns.academique.application.controllers.UniversiteController',
    r'com\.backend\.gns\.Shared\.application\.dtos\.requests\.UniversiteRequest': 'com.backend.gns.academique.application.dtos.requests.UniversiteRequest',
    r'com\.backend\.gns\.Shared\.application\.dtos\.responses\.UniversiteResponse': 'com.backend.gns.academique.application.dtos.responses.UniversiteResponse',
    r'com\.backend\.gns\.Shared\.application\.mappers\.UniversiteMapper': 'com.backend.gns.academique.application.mappers.UniversiteMapper',

    # ParametreGns
    r'com\.backend\.gns\.Shared\.domain\.models\.ParametreGns': 'com.backend.gns.parametrage.domain.models.ParametreGns',
    r'com\.backend\.gns\.Shared\.domain\.services\.ParametreGnsService': 'com.backend.gns.parametrage.domain.services.ParametreGnsService',
    r'com\.backend\.gns\.Shared\.domain\.services\.impl\.ParametreGnsServiceImpl': 'com.backend.gns.parametrage.domain.services.impl.ParametreGnsServiceImpl',
    r'com\.backend\.gns\.Shared\.infrastructure\.repositories\.ParametreGnsRepository': 'com.backend.gns.parametrage.infrastructure.repositories.ParametreGnsRepository',
    r'com\.backend\.gns\.Shared\.application\.controllers\.ParametreGnsController': 'com.backend.gns.parametrage.application.controllers.ParametreGnsController',
    r'com\.backend\.gns\.Shared\.application\.dtos\.requests\.ParametreGnsRequest': 'com.backend.gns.parametrage.application.dtos.requests.ParametreGnsRequest',
    r'com\.backend\.gns\.Shared\.application\.dtos\.responses\.ParametreGnsResponse': 'com.backend.gns.parametrage.application.dtos.responses.ParametreGnsResponse',
    r'com\.backend\.gns\.Shared\.application\.mappers\.ParametreGnsMapper': 'com.backend.gns.parametrage.application.mappers.ParametreGnsMapper',
    r'com\.backend\.gns\.Shared\.domain\.enums\.TypeParametreGns': 'com.backend.gns.parametrage.domain.enums.TypeParametreGns',

    # ParametreDbs
    r'com\.backend\.gns\.student\.domain\.models\.ParametreDbs': 'com.backend.gns.parametrage.domain.models.ParametreDbs',
    r'com\.backend\.gns\.student\.domain\.services\.ParametreDbsService': 'com.backend.gns.parametrage.domain.services.ParametreDbsService',
    r'com\.backend\.gns\.student\.domain\.services\.impl\.ParametreDbsServiceImpl': 'com.backend.gns.parametrage.domain.services.impl.ParametreDbsServiceImpl',
    r'com\.backend\.gns\.student\.infrastructure\.repositories\.ParametreDbsRepository': 'com.backend.gns.parametrage.infrastructure.repositories.ParametreDbsRepository',
    r'com\.backend\.gns\.student\.application\.controllers\.ParametreDbsController': 'com.backend.gns.parametrage.application.controllers.ParametreDbsController',
    r'com\.backend\.gns\.student\.application\.dtos\.requests\.ParametreDbsRequest': 'com.backend.gns.parametrage.application.dtos.requests.ParametreDbsRequest',
    r'com\.backend\.gns\.student\.application\.dtos\.responses\.ParametreDbsResponse': 'com.backend.gns.parametrage.application.dtos.responses.ParametreDbsResponse',
    r'com\.backend\.gns\.student\.application\.mappers\.ParametreDbsMapper': 'com.backend.gns.parametrage.application.mappers.ParametreDbsMapper',
    r'com\.backend\.gns\.student\.domain\.enums\.TypeParametreDbs': 'com.backend.gns.parametrage.domain.enums.TypeParametreDbs',

    # Clean up any leftover exact match to Shared that wasn't covered above
    r'com\.backend\.gns\.Shared\.(?![a-z]+)': 'com.backend.gns.core.',
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

print("Refactoring complete.")
